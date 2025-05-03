package com.bookflow.book;

import com.bookflow.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

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


}
