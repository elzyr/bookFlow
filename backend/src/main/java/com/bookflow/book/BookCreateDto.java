package com.bookflow.book;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
public class BookCreateDto {

    @NotBlank(message = "Tytuł nie może być pusty")
    private String title;

    @NotEmpty(message = "Podaj przynajmniej jednego autora")
    private List<@NotBlank(message = "Imie oraz nazwisko autora nie może byc puste") String> authorNames;

    @Min(value = 0, message = "Rok wydania nie może być pusty")
    private int yearRelease;

    @NotBlank(message = "Język nie może być pusty")
    private String language;

    @NotBlank(message = "Okładka książki nie może być pusta")
    private String jpg;

    @Positive(message = "Ilość stron musi wynosić więcej niż 0")
    private int pageCount;

    @Size(max = 7000, message = "Opis może mieć maksymalnie 7000 znaków")
    private String description;

    @NotEmpty(message = "Książka musi należeć do conajmniej jeden kategorii")
    private List<@NotBlank(message = "Nazwa kategorii nie może być pusta") String> categoryNames;

    @Positive(message = "Ilość wszystkich kopii musi być większa niż 0")
    private int totalCopies;
}