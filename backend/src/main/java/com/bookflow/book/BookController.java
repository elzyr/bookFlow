package com.bookflow.book;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;
    private final BookService bookService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllBook(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Book> pageResult = bookRepository.findAll(PageRequest.of(page,size));

        List<BookDto> books = pageResult.getContent().stream().map(book -> BookDto.builder()
                .book_id(book.getId())
                .title(book.getTitle())
                .yearRelease(book.getYearRelease())
                .language(book.getLanguage())
                .jpg(book.getJpg())
                .pageCount(book.getPageCount())
                .description(book.getDescription())
                .authors(book.getAuthors())
                .categories(book.getCategories())
                .availableCopies(book.getAvailableCopies())
                .totalCopies(book.getTotalCopies())
                .build()).toList();
        return ResponseEntity.ok(
                Map.of(
                    "content", books,
                    "currentPage", pageResult.getNumber(),
                    "totalPages", pageResult.getTotalPages(),
                    "totalElements", pageResult.getTotalElements()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> ResponseEntity.ok(
                        Map.of("content",
                                BookDto.builder()
                                .book_id(book.getId())
                                .title(book.getTitle())
                                .yearRelease(book.getYearRelease())
                                .language(book.getLanguage())
                                .jpg(book.getJpg())
                                .pageCount(book.getPageCount())
                                .description(book.getDescription())
                                .authors(book.getAuthors())
                                .categories(book.getCategories())
                                .availableCopies(book.getAvailableCopies())
                                .totalCopies(book.getTotalCopies())
                                .build()
                        )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/randomBooks")
    public ResponseEntity<?> getRandomBooks() {
        List<BookDto> books = bookService.getRandomBooks(3);
        return ResponseEntity.ok(books);
    }

}
