package com.bookflow.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBook(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Book> pageResult = bookRepository.findAll(PageRequest.of(page,size));

        List<BookDto> books = pageResult.getContent().stream().map(book -> BookDto.builder()
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
        return ResponseEntity.ok(
                Map.of(
                    "content", books,
                    "currentPage", pageResult.getNumber(),
                    "totalPages", pageResult.getTotalPages(),
                    "totalElements", pageResult.getTotalElements()
                )
        );
    }


}
