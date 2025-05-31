package com.bookflow.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> loanBook(@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        loanService.reserveBook(bookId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<LoanDto>> getLoansByStatus(@RequestParam LoanStatus status) {
        return ResponseEntity.ok(loanMapper.toDtoList(loanService.getLoansByStatus(status)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/confirmLoan")
    public ResponseEntity<Void> confirmLoan(@PathVariable Long id) {
        loanService.confirmLoan(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/confirmReturn")
    public ResponseEntity<Void> confirmReturn(@PathVariable Long id) {
        loanService.confirmReturn(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myloans")
    public ResponseEntity<List<LoanDto>> getMyLoans(@RequestParam(required = false) LoanStatus status, @AuthenticationPrincipal UserDetails userDetails) {
        List<LoanHistory> loans;
        if (status == null) {
            loans = loanService.getUserLoans(userDetails.getUsername());
        } else {
            loans = loanService.getUserLoans(status, userDetails.getUsername());
        }
        return ResponseEntity.ok(loanMapper.toDtoList(loans));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/extendTime")
    public ResponseEntity<Void> extendTime(@PathVariable Long id) {
        loanService.extendLoan(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        loanService.returnBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookLoanRankDto>> getRank() {
        return ResponseEntity.ok(loanService.findMostLoanedBook());
    }

    @GetMapping("/averageRanks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookLoanRankDto>> getAverageLoanDate(@RequestParam String fromMonth) {
        YearMonth month = YearMonth.parse(fromMonth);
        LocalDate fromDate = month.atDay(1);
        return ResponseEntity.ok(loanService.getAverageLoanedTimeFromDate(fromDate.toString()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/isLoaned")
    public boolean isBookLoaned(@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        return loanService.isBookLoanedToUser(bookId, userDetails.getUsername());
    }

    @GetMapping("/userDept")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Double> getUserDept(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.getTotalDeptForUser(userDetails.getUsername()));
    }
}
