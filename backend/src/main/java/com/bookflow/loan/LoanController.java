package com.bookflow.loan;

import com.bookflow.book.Book;
import com.bookflow.book.BookRepository;
import com.bookflow.user.User;
import com.bookflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/bookLoan")
    public ResponseEntity<?> bookLoan(@RequestParam Long bookId, @RequestParam Long userId) {

        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(book.get().getAvailableCopies() == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Available book's number equals zero");
        }

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // check if user already loan that book
        if(loanRepository.existsByBook_IdAndUser_IdAndReturnedFalse(bookId,userId)) {
            return ResponseEntity.status(HttpStatus.FOUND).body("Book already loaned");
        }

        boolean hasReturned = loanRepository.findFirstByUserIdAndReturnedFalseAndReturnDateBefore(userId, LocalDate.now()).isPresent();
        if(hasReturned) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User has expired book loaned. Cannot loan a book.");
        }

        // check id user has more than 5 book
        long loanedBook = loanRepository.countByUserIdAndReturnedFalse(userId);
        if(loanedBook > 4){
            return ResponseEntity.status(HttpStatus.FOUND).body("User has more than 5 book loaned right now");
        }

        //save loan
        LocalDate now = LocalDate.now();
        LocalDate returnDate = now.plusDays(14);

        LoanHistory loan = LoanHistory.builder()
                .user(user.get())
                .book(book.get())
                .returned(false)
                .borrowDate(now)
                .extendedTime(false)
                .returnDate(returnDate)
                .build();


        // update book copies
        Book bookLoaned = book.get();
        bookLoaned.setAvailableCopies(book.get().getAvailableCopies()-1);
        bookRepository.save(bookLoaned);

        loanRepository.save(loan);

        return ResponseEntity.ok("Book loaned successfully");
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanActive")
    public ResponseEntity<List<LoanDto>> getActiveLoans(@RequestParam Long userId) {
        List<LoanDto> result = loanService.getLoanedBooks(userId, false);
        return result.isEmpty() ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historyLoanReturned")
    public ResponseEntity<List<LoanDto>> getReturnedLoans(@RequestParam Long userId) {
        List<LoanDto> result = loanService.getLoanedBooks(userId, true);
        return result.isEmpty() ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/extendTime")
    public ResponseEntity<?> extendTime(@RequestParam Long userId, @RequestParam Long bookId) {
        if (loanRepository.existsByBookIdAndUserIdAndExtendedTimeTrueAndReturnedFalse(bookId, userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User has already extended time for this book");
        }


        List<LoanHistory> loans = loanRepository.findByUserIdAndBookIdAndReturnedFalse(userId, bookId);
        if (loans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
        }

        LoanHistory loan = loans.get(0);
        LocalDate now = LocalDate.now();


        if (loan.getReturnDate().isBefore(now)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot extend time after return date");
        }

        loan.setExtendedTime(true);
        loan.setReturnDate(loan.getReturnDate().plusDays(7));
        loanRepository.save(loan);

        return ResponseEntity.ok("User extended time successfully");
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping("/returnBook")
    public ResponseEntity<?> returnBook(@RequestParam Long userId, @RequestParam Long bookId) {
        List<LoanHistory> loans = loanRepository.findByUserIdAndBookIdAndReturnedFalse(userId, bookId);

        if (loans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
        }

        LoanHistory loan = loans.get(0);

        if (loan.isReturned()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book has already been returned");
        }

        LocalDate now = LocalDate.now();
        LocalDate returnDate = loan.getReturnDate();

        if (returnDate.isBefore(now)) {
            long overdueDays = ChronoUnit.DAYS.between(returnDate, now);
            float dept = (float) (overdueDays * 0.5);

            userRepository.findById(userId).ifPresent(u -> {
                float currentDept = u.getDept();
                u.setDept(currentDept + dept);
                userRepository.save(u);
            });
        }
        loan.setBookReturned(now);
        loan.setReturned(true);
        loanRepository.save(loan);

        bookRepository.findById(bookId).ifPresent(book -> {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        });

        return ResponseEntity.ok("Book returned successfully");
    }



}
