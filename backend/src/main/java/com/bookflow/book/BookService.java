package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.author.AuthorService;
import com.bookflow.category.Category;
import com.bookflow.category.CategoryService;
import com.bookflow.exception.DuplicateBookException;
import com.bookflow.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream().map(bookMapper::toDto).collect(Collectors.toList());
    }

    public Page<BookDto> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    public List<BookDto> getRandomBooks(int count) {
        List<Book> allBooks = bookRepository.findAll();
        Collections.shuffle(allBooks);

        return allBooks.stream().
                limit(count).
                map(bookMapper::toDto).
                collect(Collectors.toList());
    }

    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Książka nie istnieje"));
        return bookMapper.toDto(book);
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public BookDto addBook(AddBookRequest newBook) {
        List<Long> authorIds = newBook.getAuthorIds();
        List<Author> authors = authorService.findAllByIds(authorIds);

        List<Category> categories = categoryService.getAllOrCreate(newBook.getCategories());

        List<Book> podobne = bookRepository
                .findByTitleIgnoreCaseAndYearRelease(newBook.getTitle(), newBook.getYearRelease());
        Set<Long> newAuthorSet = new HashSet<>(authorIds);
        for (Book b : podobne) {
            Set<Long> existing = b.getAuthors().stream()
                    .map(Author::getAuthor_id)
                    .collect(Collectors.toSet());
            if (existing.equals(newAuthorSet)) {
                throw new DuplicateBookException(
                        "Książka o tym tytule, roku i autorach już istnieje."
                );
            }
        }

        Book book = Book.builder()
                .title(newBook.getTitle())
                .yearRelease(newBook.getYearRelease())
                .language(newBook.getLanguage())
                .jpg(newBook.getJpg())
                .pageCount(newBook.getPageCount())
                .description(newBook.getDescription())
                .authors(authors)
                .categories(categories)
                .totalCopies(newBook.getTotalCopies())
                .availableCopies(newBook.getAvailableCopies())
                .build();

        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }


    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Nie znaleziono książki"));
        book.getAuthors().forEach(author -> author.getBooks().remove(book));
        book.getCategories().forEach(category -> category.getBooks().remove(book));
        bookRepository.delete(book);
    }

    @Transactional
    public BookDto updateBook(Long id, BookDto updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono książki o id " + id));

        if (updatedBook.getCategories() != null) {
            List<Category> catsImmutable = categoryService.getAllOrCreate(updatedBook.getCategories());
            List<Category> cats = new ArrayList<>(catsImmutable);  // <<< mutable
            book.setCategories(cats);
        }

        if (updatedBook.getAuthors() != null) {
            List<Author> authImmutable = updatedBook.getAuthors().stream()
                    .map(aDto -> authorService.getById(aDto.getAuthor_id())
                            .orElseThrow(() -> new NotFoundException(
                                    "Autor nie istnieje: " + aDto.getAuthor_id())))
                    .toList();
            List<Author> authors = new ArrayList<>(authImmutable);
            book.setAuthors(authors);
        }

        book.setTitle(updatedBook.getTitle());
        book.setYearRelease(updatedBook.getYearRelease());
        book.setLanguage(updatedBook.getLanguage());
        book.setJpg(updatedBook.getJpg());
        book.setPageCount(updatedBook.getPageCount());
        book.setDescription(updatedBook.getDescription());
        book.setTotalCopies(updatedBook.getTotalCopies());
        book.setAvailableCopies(updatedBook.getAvailableCopies());

        Book updated = bookRepository.save(book);
        return bookMapper.toDto(updated);
    }

}
