package com.bookflow.category;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long categoryId;
    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    private String categoryName;
}