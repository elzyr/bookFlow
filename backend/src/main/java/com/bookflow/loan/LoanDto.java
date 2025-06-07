package com.bookflow.loan;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {
    private Long id;
    private Long bookId;
    private String title;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean extendedTime;
    private LoanStatus status;
    private LocalDate bookReturned;
    private String userEmail;
    private Float dept;
    private boolean reminderSent;
}
