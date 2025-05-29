package com.bookflow.book;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks().stream().map(bookMapper::toDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    public ResponseEntity<Page<BookDto>> listBooks(@PageableDefault(size = 5, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<BookDto> page = bookService.getBooks(pageable).map(bookMapper::toDto);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookMapper.toDto(bookService.getById(id)));
    }

    @GetMapping("/randomBooks")
    public ResponseEntity<List<BookDto>> getRandomBooks() {
        List<BookDto> books = bookService.getRandomBooks().stream().
                limit(3).
                map(bookMapper::toDto).
                collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> createBook(@RequestBody @Valid BookCreateDto dto) {
        Book saved = bookService.addBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long bookId, @RequestBody @Valid BookCreateDto dto) {
        Book updated = bookService.updateBook(bookId, dto);
        return ResponseEntity.ok(updated);
    }

}
