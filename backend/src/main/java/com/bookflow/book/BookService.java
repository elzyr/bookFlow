package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.author.AuthorService;
import com.bookflow.category.Category;
import com.bookflow.category.CategoryService;
import com.bookflow.exception.DuplicateBookException;
import com.bookflow.exception.IncorrectNumberOfBooksException;
import com.bookflow.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }


    public Page<Book> getBooks(Pageable pageable, String search, String filterField) {
        List<Book> books = bookRepository.findAll();

        if (search != null && !search.trim().isEmpty()) {
            String lowerSearch = search.toLowerCase();

            books = books.stream()
                    .filter(b -> {
                        if ("title".equals(filterField)) {
                            return b.getTitle().toLowerCase().contains(lowerSearch);
                        } else if ("authors.name".equals(filterField)) {
                            return b.getAuthors().stream().anyMatch(a ->
                                    a.getName().toLowerCase().contains(lowerSearch)
                            );
                        }
                        return false;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            Comparator<Book> comparator = switch (order.getProperty()) {
                case "title" -> Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
                case "authors.name" -> Comparator.comparing(
                        b -> b.getAuthors().isEmpty() ? "" : b.getAuthors().get(0).getName(), String.CASE_INSENSITIVE_ORDER);
                default -> null;
            };

            if (comparator != null) {
                if (order.getDirection().isDescending()) {
                    comparator = comparator.reversed();
                }
                books.sort(comparator);
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), books.size());
        List<Book> pageContent = start < end ? books.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, books.size());
    }


    public List<Book> getRandomBooks() {
        List<Book> allBooks = bookRepository.findAll();
        Collections.shuffle(allBooks);
        return allBooks;
    }

    public Book getById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Książka nie istnieje"));
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public Book addBook(BookCreateDto dto) {
        List<Author> authors = authorService.validateAuthors(dto.getAuthorNames());
        List<Category> categories = categoryService.getAllOrCreateCategories(dto.getCategoryNames());

        String title = dto.getTitle().trim();

        List<Book> existingByTitle = bookRepository.findByTitleIgnoreCase(title);
        for (Book existing : existingByTitle) {
            if (existing.getAuthors().containsAll(authors) && authors.containsAll(existing.getAuthors())) {
                throw new DuplicateBookException(
                        "Książka o tytule '" + title + "' z podanymi autorami już istnieje (ID: " + existing.getId() + ")"
                );
            }
        }

        Book book = Book.builder()
                .title(title)
                .authors(authors)
                .yearRelease(dto.getYearRelease())
                .language(dto.getLanguage())
                .jpg(dto.getJpg())
                .pageCount(dto.getPageCount())
                .description(dto.getDescription())
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getTotalCopies())
                .categories(categories)
                .build();


        // add books to authors
        authors.forEach(a -> {
            if (a.getBooks() == null) {
                a.setBooks(new ArrayList<>());
            }
            a.getBooks().add(book);
        });

        // add book to categories
        categories.forEach(c -> {
            if (c.getBooks() == null) {
                c.setBooks(new ArrayList<>());
            }
            c.getBooks().add(book);
        });
        return bookRepository.save(book);
    }


    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Nie znaleziono książki"));
        book.getAuthors().forEach(author -> author.getBooks().remove(book));
        book.getCategories().forEach(category -> category.getBooks().remove(book));
        bookRepository.delete(book);
    }

    @Transactional
    public Book updateBook(Long bookId, BookCreateDto dto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono książki o ID: " + bookId));

        List<Author> newAuthors = authorService.validateAuthors(dto.getAuthorNames());
        List<Category> newCategories = categoryService.getAllOrCreateCategories(dto.getCategoryNames());

        String newTitle = dto.getTitle().trim();
        List<Book> sameTitleBooks = bookRepository.findByTitleIgnoreCase(newTitle);
        for (Book existing : sameTitleBooks) {
            if (!existing.getId().equals(bookId)
                    && existing.getAuthors().containsAll(newAuthors)
                    && newAuthors.containsAll(existing.getAuthors())) {
                throw new IllegalArgumentException(
                        "Inna książka o tytule '" + newTitle + "' z podanymi autorami już istnieje (ID: " + existing.getId() + ")"
                );
            }
        }

        book.setTitle(newTitle);
        book.setYearRelease(dto.getYearRelease());
        book.setLanguage(dto.getLanguage());
        book.setJpg(dto.getJpg());
        book.setPageCount(dto.getPageCount());
        book.setDescription(dto.getDescription());

        List<Author> oldAuthors = new ArrayList<>(book.getAuthors());
        oldAuthors.removeAll(newAuthors);
        oldAuthors.forEach(a -> a.getBooks().remove(book));

        book.getAuthors().clear();
        book.getAuthors().addAll(newAuthors);
        newAuthors.forEach(a -> {
            if (a.getBooks() == null) {
                a.setBooks(new ArrayList<>());
            }
            if (!a.getBooks().contains(book)) {
                a.getBooks().add(book);
            }
        });

        List<Category> oldCategories = new ArrayList<>(book.getCategories());
        oldCategories.removeAll(newCategories);
        oldCategories.forEach(c -> c.getBooks().remove(book));

        book.getCategories().clear();
        book.getCategories().addAll(newCategories);
        newCategories.forEach(c -> {
            if (c.getBooks() == null) {
                c.setBooks(new ArrayList<>());
            }
            if (!c.getBooks().contains(book)) {
                c.getBooks().add(book);
            }
        });

        int oldTotal = book.getTotalCopies();
        int oldAvailable = book.getAvailableCopies();
        int newTotal = dto.getTotalCopies();
        if (newTotal < oldTotal - oldAvailable) {
            throw new IncorrectNumberOfBooksException(
                    "Nie możesz zmniejszyć ilości książek poniżej liczby wypożyczonych"
            );
        }
        book.setTotalCopies(newTotal);
        int delta = newTotal - oldTotal;
        book.setAvailableCopies(Math.max(0, oldAvailable + delta));

        return bookRepository.save(book);
    }

}
