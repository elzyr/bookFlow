package com.bookflow.author;

import com.bookflow.book.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@Entity
@Table(name = "book_authors")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int author_id;

    @NonNull
    @Column(unique = true)
    private String name;

    private String information;

    @ManyToMany(mappedBy = "authors")
    Set<Book> books;
}
