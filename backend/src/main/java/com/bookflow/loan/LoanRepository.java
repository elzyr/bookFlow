package com.bookflow.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanHistory, Long> {


    List<LoanHistory> findByUser_UsernameAndReturnedFalse(String username);

    List<LoanHistory> findByUser_UsernameAndReturnedTrue(String username);


    @Query("SELECT new com.bookflow.loan.BookLoanRankDto(l.book.title, COUNT(l)) " +
            "FROM LoanHistory l GROUP BY l.book.title ORDER BY COUNT(l) DESC")
    List<BookLoanRankDto> findMostLoanedBooks();

}
