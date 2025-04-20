package com.bookflow.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanHistory,Long> {

    boolean existsByBook_IdAndUser_IdAndReturnedFalse(Long bookId, Long userId);

    Optional<LoanHistory> findFirstByUserIdAndReturnedFalseAndReturnDateBefore(Long userId, LocalDate date);

    long countByUserIdAndReturnedFalse(Long userId);

    List<LoanHistory> findByUserIdAndReturnedFalse(Long userId);

    List<LoanHistory> findByUserIdAndReturnedTrue(Long userId);

    boolean existsByBookIdAndUserIdAndExtendedTimeTrue(Long book_id, Long user_id);

    LoanHistory findByUserIdAndBookId(Long user_id, Long book_id);

}
