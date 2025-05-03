package com.bookflow.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/loanBook")
    public ResponseEntity<?> bookLoan(@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        loanService.loanBook(bookId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanActive")
    public ResponseEntity<List<LoanDto>> getActiveLoans(@AuthenticationPrincipal UserDetails userDetails) {
        List<LoanDto> result = loanService.getLoanedBooks(userDetails.getUsername(), false);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanReturned")
    public ResponseEntity<List<LoanDto>> getReturnedLoans(@AuthenticationPrincipal UserDetails userDetails) {
        List<LoanDto> result = loanService.getLoanedBooks(userDetails.getUsername(), true);
        return result.isEmpty() ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/extendTime")
    public ResponseEntity<?> extendTime(@PathVariable Long id) {
        loanService.extendLoan(id);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        loanService.returnBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranks")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookLoanRankDto> getRank() {
        return loanService.findMostLoanedBook();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LoanDto>> getAllLoans(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<LoanDto> loans = loanService.getLoanedBooks(username);
        return ResponseEntity.ok(loans);
    }


}
