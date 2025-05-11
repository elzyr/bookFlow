package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.author.AuthorService;
import com.bookflow.category.Category;
import com.bookflow.category.CategoryService;
import com.bookflow.exception.DuplicateBookException;
import com.bookflow.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
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
    private final ReferenceDataService referenceDataService;

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
    public Book addBook(BookCreateDto dto) {
        // 1. Rozwiąż autorów i kategorie (utwórz nowe jeśli nie istnieją)
        List<Author> authors = referenceDataService.resolveAuthors(dto.getAuthorNames());
        List<Category> categories = referenceDataService.resolveCategories(dto.getCategoryNames());

        // 2. Zbuduj encję Book
        Book book = Book.builder()
                .title(dto.getTitle())
                .authors(authors)
                .yearRelease(dto.getYearRelease())
                .language(dto.getLanguage())
                .jpg(dto.getJpg())
                .pageCount(dto.getPageCount())
                .description(dto.getDescription())
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getTotalCopies())   // przy dodaniu wszystkie dostępne
                .build();

        // 3. Ustaw kategorie dwukierunkowo
        book.setCategories(categories);
        categories.forEach(cat -> {
            if (!cat.getBooks().contains(book)) {
                cat.getBooks().add(book);
            }
        });

        // 4. Zapisz i zwróć
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
        // 1. Pobierz istniejącą książkę
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookId));

        // 2. Rozwiąż autorów i kategorie
        List<Author> newAuthors = referenceDataService.resolveAuthors(dto.getAuthorNames());
        List<Category> newCategories = referenceDataService.resolveCategories(dto.getCategoryNames());

        // 3. Aktualizuj proste pola
        book.setTitle(dto.getTitle());
        book.setYearRelease(dto.getYearRelease());
        book.setLanguage(dto.getLanguage());
        book.setJpg(dto.getJpg());
        book.setPageCount(dto.getPageCount());
        book.setDescription(dto.getDescription());

        // 4. Synchronizacja autorów
        book.getAuthors().clear();
        book.getAuthors().addAll(newAuthors);
        newAuthors.forEach(a -> {
            if (!a.getBooks().contains(book)) {
                a.getBooks().add(book);
            }
        });

        // 5. Synchronizacja kategorii
        book.getCategories().clear();
        book.getCategories().addAll(newCategories);
        newCategories.forEach(c -> {
            if (!c.getBooks().contains(book)) {
                c.getBooks().add(book);
            }
        });

        // 6. Uaktualnij liczbę kopii
        int oldTotal = book.getTotalCopies();
        int oldAvailable = book.getAvailableCopies();
        int newTotal = dto.getTotalCopies();
        book.setTotalCopies(newTotal);
        // jeśli zmniejszamy całkowitą liczbę, zmniejszamy też dostępne (ale nie poniżej 0)
        int delta = newTotal - oldTotal;
        book.setAvailableCopies(Math.max(0, oldAvailable + delta));

        // 7. Zapisz zmiany
        return bookRepository.save(book);
    }

}
