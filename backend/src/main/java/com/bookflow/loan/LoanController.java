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
    public ResponseEntity<Void> bookLoan(@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        loanService.loanBook(bookId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanActive")
    public ResponseEntity<List<LoanDto>> getActiveLoans(@AuthenticationPrincipal UserDetails userDetails) {
        List<LoanDto> result = loanMapper.toDtoList(loanService.getLoanedBooks(userDetails.getUsername(), false));
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanReturned")
    public ResponseEntity<List<LoanDto>> getReturnedLoans(@AuthenticationPrincipal UserDetails userDetails) {
        List<LoanDto> result = loanMapper.toDtoList(loanService.getLoanedBooks(userDetails.getUsername(), true));
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/extendTime")
    public ResponseEntity<Void> extendTime(@PathVariable Long id) {
        loanService.extendLoan(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
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
    public ResponseEntity<List<LoanDto>> getAllUserLoans(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<LoanDto> loans = loanMapper.toDtoList(loanService.getLoanedBooks(username, false));
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/averageRanks")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookLoanRankDto> getAverageLoanDate(@RequestParam String fromMonth) {
        YearMonth month = YearMonth.parse(fromMonth);
        LocalDate fromDate = month.atDay(1);
        return loanService.getAverageLoanedTimeFromDate(fromDate.toString());
    }

    @GetMapping("/isLoaned")
    public boolean isBookLoaned(@RequestParam Long bookId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return loanService.isBookLoanedToUser(bookId, username);
    }

    @GetMapping("/userDept")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Double> getUserDept(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        double dept = loanService.getTotalDeptForUser(username);
        return ResponseEntity.ok(dept);
    }

}
