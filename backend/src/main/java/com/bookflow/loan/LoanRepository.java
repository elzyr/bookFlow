package com.bookflow.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanHistory,Long> {

    boolean existsByBook_IdAndUser_IdAndReturnedFalse(Long bookId, Long userId);

    Optional<LoanHistory> findFirstByUserIdAndReturnedFalseAndReturnDateAfter(Long userId, LocalDate date);

    long countByUserIdAndReturnedFalse(Long userId);

}
