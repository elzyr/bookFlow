package com.bookflow.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/bookLoan")
    public ResponseEntity<?> bookLoan(@RequestParam Long bookId, @RequestParam String username) {
        loanService.bookLoan(bookId, username);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanActive")
    public ResponseEntity<List<LoanDto>> getActiveLoans(@RequestParam String username) {
        List<LoanDto> result = loanService.getLoanedBooks(username, false);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanReturned")
    public ResponseEntity<List<LoanDto>> getReturnedLoans(@RequestParam String username) {
        List<LoanDto> result = loanService.getLoanedBooks(username, true);
        return result.isEmpty() ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/extendTime")
    public ResponseEntity<?> extendTime(@RequestParam String username, @RequestParam Long bookId) {
            loanService.extendLoan(username, bookId);
            return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping("/returnBook")
    public ResponseEntity<?> returnBook(@RequestParam String username, @RequestParam Long bookId) {
        loanService.returnBook(username, bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranks")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookLoanRankDto> getRank() {
        return loanService.findMostLoanedBook();
    }

}
