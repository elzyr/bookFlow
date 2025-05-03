package com.bookflow.loan;

import com.bookflow.book.Book;
import com.bookflow.book.BookService;
import com.bookflow.exception.ExtensionNotAllowedException;
import com.bookflow.exception.LoanInvalidException;
import com.bookflow.exception.NotFoundException;
import com.bookflow.user.User;
import com.bookflow.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final LoanMapper loanMapper;

    private static final int LOAN_DURATION_DAYS = 14;
    private static final int BOOK_UNAVAILABLE = 0;
    private static final int MAXIMUM_LOAN_BOOK_NUMBER = 5;
    private static final float MONEY_TO_PAY_FOR_DAY = (float)0.5;
    private static final int NUMBER_COPY = 1;

    public List<LoanDto> getLoanedBooks(String username, boolean returned) {
        List<LoanHistory> loans = returned
                ? loanRepository.findByUser_UsernameAndReturnedTrue(username)
                : loanRepository.findByUser_UsernameAndReturnedFalse(username);

        return loans
                .stream()
                .map(loanMapper::toDto).toList();
    }

    public void extendLoan(String username, Long bookId) {
        if (loanRepository.existsByBook_IdAndUser_UsernameAndExtendedTimeTrueAndReturnedFalse(bookId, username)) {
            throw new LoanInvalidException("Book already extended");
        }

        LoanHistory loan = loanRepository.findByUser_UsernameAndBook_Id(username, bookId)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getReturnDate().isBefore(LocalDate.now())) {
            throw new ExtensionNotAllowedException("Cannot extend loan after return date");
        }

        loan.setExtendedTime(true);
        loan.setReturnDate(loan.getReturnDate().plusDays(LOAN_DURATION_DAYS / 2));
        loanRepository.save(loan);
    }

    public void returnBook(String username, Long bookId) {
        List<LoanHistory> loans = loanRepository.findByUser_UsernameAndReturnedFalse(username);

        if (loans.isEmpty()) { throw new NotFoundException("Book not found"); }

        LoanHistory loan = loans.stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.isReturned()) {
           throw new ExtensionNotAllowedException("Book already returned");
        }

        LocalDate now = LocalDate.now();
        LocalDate returnDate = loan.getReturnDate();

        if (returnDate.isBefore(now)) {
            long overdueDays = ChronoUnit.DAYS.between(returnDate, now);
            float dept = (float) (overdueDays * MONEY_TO_PAY_FOR_DAY);

            User user = userService.findByUsernameEntity(username);
            float currentDept = user.getDept();
            user.setDept(currentDept + dept);
            userService.save(user);
        }
        loan.setBookReturned(now);
        loan.setReturned(true);
        loanRepository.save(loan);

        Book book = bookService.getById(bookId);
        book.setAvailableCopies(book.getAvailableCopies() + NUMBER_COPY);
        bookService.saveBook(book);
    }

    public void bookLoan(Long bookId, String username) {
        Book book = bookService.getById(bookId);

        if(book.getAvailableCopies() == BOOK_UNAVAILABLE){
            throw new LoanInvalidException("Available book's number equals zero");
        }

        User user = userService.findByUsernameEntity(username);

        // check if user already loan that book
        if(loanRepository.existsByBook_IdAndUser_UsernameAndReturnedFalse(bookId,username)) {
           throw new LoanInvalidException("Book already loaned");
        }

        boolean hasReturned = loanRepository.findFirstByUser_UsernameAndReturnedFalseAndReturnDateBefore(username, LocalDate.now()).isPresent();
        if(hasReturned) {
            throw new LoanInvalidException("User has expired book loaned. Cannot loan a book.");
        }

        // check id user has more than 5 book
        long loanedBook = loanRepository.countByUser_UsernameAndReturnedFalse(username);
        if(loanedBook >= MAXIMUM_LOAN_BOOK_NUMBER){
            throw new LoanInvalidException("User has 5 book loaned right now");
        }

        //save loan
        LocalDate now = LocalDate.now();
        LocalDate returnDate = now.plusDays(LOAN_DURATION_DAYS);

        LoanHistory loan = new LoanHistory();
        loan.setReturnDate(returnDate);
        loan.setReturned(false);
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(now);
        loan.setExtendedTime(false);


        // update book copies
        int numberCopies = book.getAvailableCopies();
        book.setAvailableCopies(numberCopies - NUMBER_COPY);
        bookService.saveBook(book);
        loanRepository.save(loan);
    }

    public List<BookLoanRankDto> findMostLoanedBook(){
        return loanRepository.findMostLoanedBooks();
    }

}
