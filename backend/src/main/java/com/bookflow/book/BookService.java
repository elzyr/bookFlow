package com.bookflow.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDto> getRandomBooks(int count) {
        List<Book> allBooks = bookRepository.findAll();
        Collections.shuffle(allBooks);

        return allBooks.stream()
                .limit(count)
                .map(book -> BookDto.builder()
                        .book_id(book.getId())
                        .title(book.getTitle())
                        .jpg(book.getJpg())
                        .description(book.getDescription())
                        .language(book.getLanguage())
                        .build())
                .collect(Collectors.toList());
    }
}
