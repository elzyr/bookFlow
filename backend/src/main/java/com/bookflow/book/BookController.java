package com.bookflow.book;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBook() {
        List<BookDto> books = bookRepository.findAll().stream().map(book -> BookDto.builder()
                .book_id(book.getBook_id())
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
        return ResponseEntity.ok(books);
    }


}
