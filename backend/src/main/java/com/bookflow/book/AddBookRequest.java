package com.bookflow.book;

import com.bookflow.category.CategoryInputDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBookRequest {

    @NotBlank(message = "Tytuł nie może być pusty")
    private String title;

    @Min(value = 0, message = "Rok wydania nie może być ujemny")
    private int yearRelease;

    @NotBlank(message = "Język nie może być pusty")
    private String language;

    @Pattern(regexp = "https?://.*\\.(png|jpg|jpeg)$", message = "Nieprawidłowy URL okładki")
    private String jpg;

    @Min(value = 1, message = "Liczba stron musi być co najmniej 1")
    private int pageCount;

    @NotBlank(message = "Opis nie może być pusty")
    @Size(max = 2000, message = "Opis może mieć maksymalnie {max} znaków")
    private String description;

    @NotEmpty(message = "Przynajmniej jeden autor musi być podany")
    private List<@NotNull(message = "Id autora nie może być null") Long> authorIds;

    @NotEmpty(message = "Przynajmniej jedna kategoria musi być podana")
    private List<@Valid CategoryInputDto> categories;

    @PositiveOrZero(message = "Łączna liczba kopii nie może być ujemna")
    private int totalCopies;

    @PositiveOrZero(message = "Dostępne kopie nie mogą być ujemne")
    private int availableCopies;
}
