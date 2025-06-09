package com.bookflow.loan;

import com.bookflow.book.Book;
import com.bookflow.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loan_history")
public class LoanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(nullable = false)
    private boolean extendedTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private float debt = 0f;

    private LocalDate bookReturned;

    @Column(nullable = false)
    private boolean reminderSent = false;
}
