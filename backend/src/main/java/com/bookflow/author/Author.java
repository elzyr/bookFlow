package com.bookflow.author;

import com.bookflow.book.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "author")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int author_id;

    @NonNull
    @Column(unique = true)
    private String name;

    @Column(length = 1000)
    private String information;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    @Column(name = "author_jpg")
    private String author_jpg;
}
