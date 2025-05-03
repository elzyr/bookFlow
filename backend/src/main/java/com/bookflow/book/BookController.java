package com.bookflow.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;


@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BookDto>> listBooks(
            @PageableDefault(size = 5, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<BookDto> page = bookService.getBooks(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping("/randomBooks")
    public ResponseEntity<?> getRandomBooks() {
        List<BookDto> books = bookService.getRandomBooks(3);
        return ResponseEntity.ok(books);
    }

}
