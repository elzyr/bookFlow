package com.bookflow.loan;

import com.bookflow.book.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanHistory, Long> {

    List<LoanHistory> findByUser_UsernameAndReturnedFalse(String username);

    List<LoanHistory> findByUser_UsernameAndReturnedTrue(String username);

    List<LoanHistory> findAllByReturnedTrue();
}
