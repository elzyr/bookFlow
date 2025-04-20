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
import java.util.Collections;
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
        return result.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
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
        if (loanRepository.existsByBookIdAndUserIdAndExtendedTimeTrue(bookId, userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User has already extended time for this book");
        }

        LoanHistory loan = loanRepository.findByUserIdAndBookId(userId, bookId);
        if (loan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
        }
        LocalDate now = LocalDate.now();

        if (loan.getReturnDate().isBefore(now)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot extend time after return date");
        }

        loan.setExtendedTime(true);
        loan.setReturnDate(now.plusDays(7));
        loanRepository.save(loan);

        return ResponseEntity.ok("User extended time successfully");
    }

}
