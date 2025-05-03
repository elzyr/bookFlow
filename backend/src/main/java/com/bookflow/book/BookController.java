package com.bookflow.book;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    public ResponseEntity<Page<BookDto>> listBooks(
            @PageableDefault(size = 5, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<BookDto> page = bookService.getBooks(pageable);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @GetMapping("/randomBooks")
    public ResponseEntity<List<BookDto>> getRandomBooks() {
        List<BookDto> books = bookService.getRandomBooks(3);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addBook(@RequestBody @Valid AddBookRequest newBook) {
        BookDto created = bookService.addBook(newBook);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getBook_id()).toUri();
        return ResponseEntity.created(location).build();
    }

}
