package com.bookflow.loan;

import com.bookflow.book.Book;
import com.bookflow.book.BookMapper;
import com.bookflow.book.BookService;
import com.bookflow.exception.ExtensionNotAllowedException;
import com.bookflow.exception.LoanInvalidException;
import com.bookflow.exception.NotFoundException;
import com.bookflow.user.User;
import com.bookflow.user.UserDto;
import com.bookflow.user.UserMapper;
import com.bookflow.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final LoanMapper loanMapper;
    private final UserMapper userMapper;

    private static final int LOAN_DURATION_DAYS = 14;
    private static final int BOOK_UNAVAILABLE = 0;
    private static final int MAXIMUM_LOAN_BOOK_NUMBER = 5;
    private static final float MONEY_TO_PAY_FOR_DAY = (float) 0.5;
    private static final int LOANED_BOOK = 1;
    private final BookMapper bookMapper;

    public List<LoanDto> getLoanedBooks(String username, boolean returned) {
        List<LoanHistory> loans = returned
                ? loanRepository.findByUser_UsernameAndReturnedTrue(username)
                : loanRepository.findByUser_UsernameAndReturnedFalse(username);

        if (loans.isEmpty()) {
            throw new NotFoundException("Nie znaleziono ksiazek dla " + username);
        }

        return loans
                .stream()
                .map(loanMapper::toDto).toList();
    }

    public void extendLoan(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Nie znaleziono wypożyczenia"));

        if (loan.isExtendedTime() && !loan.isReturned()) {
            throw new LoanInvalidException("Książka jest już wypożyczona");
        }

        if (loan.getReturnDate().isBefore(LocalDate.now())) {
            throw new ExtensionNotAllowedException("Nie można przedłużyć wypożyczenia po terminie zwrotu");
        }

        loan.setExtendedTime(true);
        loan.setReturnDate(loan.getReturnDate().plusDays(LOAN_DURATION_DAYS / 2));
        loanRepository.save(loan);
    }

    public void returnBook(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Nie znaleziono wypożyczenia"));

        if (loan.isReturned()) {
            throw new ExtensionNotAllowedException("Książka jest już zwrócona");
        }

        LocalDate now = LocalDate.now();
        LocalDate returnDate = loan.getReturnDate();

        if (returnDate.isBefore(now)) {
            long overdueDays = ChronoUnit.DAYS.between(returnDate, now);
            float dept = (float) (overdueDays * MONEY_TO_PAY_FOR_DAY);
            loan.setDept(loan.getDept() + dept);
        }
        loan.setBookReturned(now);
        loan.setReturned(true);
        loanRepository.save(loan);

        Book book = bookMapper.toEntity(bookService.getById(loan.getBook().getId()));
        book.setAvailableCopies(book.getAvailableCopies() + LOANED_BOOK);
        bookService.saveBook(book);
    }

    public void loanBook(Long bookId, String username) {
        Book book = bookMapper.toEntity(bookService.getById(bookId));

        if (book.getAvailableCopies() == BOOK_UNAVAILABLE) {
            throw new LoanInvalidException("Wszystkie kopie danej książki są już wypożyczone");
        }

        UserDto userDto = userService.findByUsername(username);
        User user = userMapper.toEntity(userDto);

        List<LoanHistory> loans = loanRepository.findByUser_UsernameAndReturnedFalse(username);
        boolean isBorrowed = loans.stream()
                .anyMatch(l -> l.getBook().getId().equals(bookId));

        if (isBorrowed) {
            throw new LoanInvalidException("Książka jest już wypożyczona");
        }

        boolean hasExpired = loans.stream()
                .filter(l -> !l.isReturned())
                .anyMatch(l -> l.getReturnDate().isBefore(LocalDate.now()));

        if (hasExpired) {
            throw new LoanInvalidException("Nie niezwrócone ksiązki. Nie można wypożyczyć nowej");
        }


        if (loans.size() >= MAXIMUM_LOAN_BOOK_NUMBER) {
            throw new LoanInvalidException("Masz maksymalną ilość wypożyczonych książek (" + MAXIMUM_LOAN_BOOK_NUMBER + ")");
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
        book.setAvailableCopies(numberCopies - LOANED_BOOK);
        bookService.saveBook(book);
        loanRepository.save(loan);
    }

    public List<BookLoanRankDto> findMostLoanedBook() {
        return loanRepository.findMostLoanedBooks();
    }

    public List<LoanDto> getLoanedBooks(String username) {
        List<LoanHistory> loans = loanRepository
                .findByUser_UsernameAndReturnedFalse(username);

        if (loans.isEmpty()) {
            throw new LoanInvalidException("Nie masz żadnych wypożyczeń");
        }

        return loans.stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookLoanRankDto> averageLoanedTime() {
        return loanRepository.findAverageLoanDurationPerBook();
    }


}
