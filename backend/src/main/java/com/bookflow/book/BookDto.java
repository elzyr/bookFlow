package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.category.Category;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookDto {
    public Long book_id;
    public String title;
    public int yearRelease;
    public String language;
    public String jpg;
    public int pageCount;
    public String description;
    public List<Author> authors;
    public List<Category> categories;
    public int totalCopies;
    public int availableCopies;
}
