package com.bookflow.loan;

import com.bookflow.book.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.domain.Page;

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
    public ResponseEntity<PageDto<LoanDto>> getLoansByStatus(
            @RequestParam LoanStatus status,
            @PageableDefault(size = 5, sort = "borrowDate") Pageable pageable
    ) {
        Page<LoanDto> page = loanService.getLoansByStatus(status, pageable)
                .map(loanMapper::toDto);

        PageDto<LoanDto> dto = new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(dto);
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelLoan(@PathVariable Long id) {
        loanService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ranks")
    public ResponseEntity<List<BookLoanRankDto>> getRank() {
        return ResponseEntity.ok(loanService.findMostLoanedBook());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/averageRanks")
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

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/userDebt")
    public ResponseEntity<Double> getUserDebt(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.getTotalDebtForUser(userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reminder-list")
    public ResponseEntity<List<LoanDto>> getSoonDueLoans(
            @RequestParam(defaultValue = "3") int daysBefore) {
        return ResponseEntity.ok(
                loanMapper.toDtoList(loanService.getLoansToBeReturnedSoon(daysBefore))
        );
    }


}
