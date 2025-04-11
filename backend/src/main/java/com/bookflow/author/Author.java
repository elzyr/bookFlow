package com.bookflow.author;

import com.bookflow.book.model.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@Entity
@Table(name = "author")
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
    private Set<Book> books;
}
