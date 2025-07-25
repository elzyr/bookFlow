package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.category.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "book")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="book_id")
    private Long id;

    @NonNull
    private String title;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    private int yearRelease;

    private String language;

    private String jpg;

    private int pageCount;

    @Column(length = 7000)
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "books")
    private List<Category> categories;

    @Column(name = "total_copies",nullable = false)
    private int totalCopies;

    @Column(name = "available_copies",nullable = false)
    private int availableCopies;

}
