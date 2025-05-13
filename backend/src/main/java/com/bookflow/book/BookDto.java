package com.bookflow.book;

import com.bookflow.author.AuthorDto;
import com.bookflow.category.CategoryDto;
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
    public List<AuthorDto> authors;
    public List<CategoryDto> categories;
    public int totalCopies;
    public int availableCopies;
}
