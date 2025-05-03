package com.bookflow.book;

import com.bookflow.exception.NotFoundException;
import com.bookflow.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookDto> getRandomBooks(int count) {
        List<Book> allBooks = bookRepository.findAll();
        Collections.shuffle(allBooks);

        return  allBooks.stream().
                limit(count).
                map(bookMapper::toDto).
                collect(Collectors.toList());
    }

    public Book getById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public void saveBook (Book book) {
        bookRepository.save(book);
    }


}
