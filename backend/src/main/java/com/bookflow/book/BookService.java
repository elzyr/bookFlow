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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

}
