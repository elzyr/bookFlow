package com.bookflow.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanHistory, Long> {

    boolean existsByBook_IdAndUser_UsernameAndReturnedFalse(Long bookId, String username);

    Optional<LoanHistory> findFirstByUser_UsernameAndReturnedFalseAndReturnDateBefore(String username, LocalDate date);

    long countByUser_UsernameAndReturnedFalse(String username);

    List<LoanHistory> findByUser_UsernameAndReturnedFalse(String username);

    List<LoanHistory> findByUser_UsernameAndReturnedTrue(String username);

    boolean existsByBook_IdAndUser_UsernameAndExtendedTimeTrueAndReturnedFalse(Long bookId, String username);

    List<LoanHistory> findByUser_UsernameAndBook_Id(String username, Long bookId);


    @Query("SELECT new com.bookflow.loan.BookLoanRankDto(l.book.title, COUNT(l)) " +
            "FROM LoanHistory l GROUP BY l.book.title ORDER BY COUNT(l) DESC")
    List<BookLoanRankDto> findMostLoanedBooks();

}
