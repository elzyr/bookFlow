package com.bookflow.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanHistory, Long> {
    List<LoanHistory> findByUser_UsernameAndStatus(String username, LoanStatus status);

    List<LoanHistory> findAllByStatus(LoanStatus status);

    boolean existsByBook_IdAndUser_UsernameAndStatusIsNot(Long bookId, String username, LoanStatus statuses);

    List<LoanHistory> findByUser_Username(String username);

    List<LoanHistory> findByUser_UsernameAndStatusNotIn(String username, List<LoanStatus> excludedStatuses);

    List<LoanHistory> findByStatusAndBorrowDateBefore(LoanStatus status, LocalDate borrowDate);

    Page<LoanHistory> findByStatus(LoanStatus status, Pageable pageable);

}
