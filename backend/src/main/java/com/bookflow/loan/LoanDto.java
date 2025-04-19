package com.bookflow.loan;

import com.bookflow.book.Book;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoanDto {
    private String title;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean extendedTime;
    private boolean returned;
}
