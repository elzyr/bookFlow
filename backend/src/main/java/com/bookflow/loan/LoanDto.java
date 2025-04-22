package com.bookflow.loan;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoanDto {
    private Integer id;
    private String title;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean extendedTime;
    private boolean returned;
    private LocalDate bookReturned;
}
