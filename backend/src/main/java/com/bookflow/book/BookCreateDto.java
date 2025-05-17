package com.bookflow.book;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
public class BookCreateDto {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotEmpty(message = "At least one author is required")
    private List<@NotBlank(message = "Author name must not be blank") String> authorNames;

    @Min(value = 0, message = "Year of release must be a positive number or zero")
    private int yearRelease;

    @NotBlank(message = "Language must not be blank")
    private String language;

    @NotBlank(message = "Cover image path must not be blank")
    private String jpg;

    @Positive(message = "Page count must be greater than zero")
    private int pageCount;

    @Size(max = 7000, message = "Description cannot exceed 7000 characters")
    private String description;

    @NotEmpty(message = "At least one category is required")
    private List<@NotBlank(message = "Category name must not be blank") String> categoryNames;

    @Positive(message = "Total copies must be greater than zero")
    private int totalCopies;
}