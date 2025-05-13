package com.bookflow.loan;

import com.bookflow.book.Book;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;

    private static final int LOAN_DURATION_DAYS = 14;
    private static final int BOOK_UNAVAILABLE = 0;
    private static final int MAXIMUM_LOAN_BOOK_NUMBER = 5;
    private static final float MONEY_TO_PAY_FOR_DAY = (float) 0.5;
    private static final int LOANED_BOOK = 1;
    private static final int RANKING_BOOK = 3;
    private static final int RANKING_BOOK_DURATION = 5;

    public List<LoanHistory> getLoanedBooks(String username, boolean returned) {
        List<LoanHistory> loans = returned
                ? loanRepository.findByUser_UsernameAndReturnedTrue(username)
                : loanRepository.findByUser_UsernameAndReturnedFalse(username);

        if (loans.isEmpty()) {
            throw new NotFoundException("Nie znaleziono ksiazek dla " + username);
        }

        return loans;
    }

    public void extendLoan(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Nie znaleziono wypożyczenia"));

        if (loan.isExtendedTime() && !loan.isReturned()) {
            throw new LoanInvalidException("Książka jest już przedłużona");
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

        Book book =bookService.getById(loan.getBook().getId());
        book.setAvailableCopies(book.getAvailableCopies() + LOANED_BOOK);
        bookService.saveBook(book);
    }

    public void loanBook(Long bookId, String username) {
        Book book = bookService.getById(bookId);

        if (book.getAvailableCopies() == BOOK_UNAVAILABLE) {
            throw new LoanInvalidException("Wszystkie kopie danej książki są już wypożyczone");
        }

        User user = userService.findByUsername(username);

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
        List<LoanHistory> loanedBooks = loanRepository.findAll();

        Map<String, Long> mostLoanedBooks = loanedBooks.stream()
                .collect(Collectors.groupingBy(
                        loan -> loan.getBook().getTitle(),
                        Collectors.counting()
                ));

        return mostLoanedBooks.entrySet().stream()
                .map(entry -> new BookLoanRankDto(entry.getKey(), entry.getValue().doubleValue()))
                .sorted(Comparator.comparing(BookLoanRankDto::getLoanCount).reversed())
                .limit(RANKING_BOOK)
                .collect(Collectors.toList());
    }

    public List<LoanHistory> getLoanedBooks(String username) {
        List<LoanHistory> loans = loanRepository
                .findByUser_UsernameAndReturnedFalse(username);

        if (loans.isEmpty()) {
            throw new LoanInvalidException("Nie masz żadnych wypożyczeń");
        }

        return loans;
    }

    public List<BookLoanRankDto> getAverageLoanedTimeFromDate(String fromDate) {
        LocalDate from = LocalDate.parse(fromDate);

        List<LoanHistory> returnedBooks = loanRepository.findAllByReturnedTrue();

        Map<String, Double> bookAverageDays = returnedBooks.stream()
                .filter(loan -> !loan.getBorrowDate().isBefore(from))
                .collect(Collectors.groupingBy(
                        loan -> loan.getBook().getTitle(),
                        Collectors.averagingDouble(loan ->
                                ChronoUnit.DAYS.between(loan.getBorrowDate(), loan.getBookReturned())
                        )
                ));

        return bookAverageDays.entrySet().stream()
                .map(entry -> new BookLoanRankDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(BookLoanRankDto::getLoanCount).reversed())
                .limit(RANKING_BOOK_DURATION)
                .collect(Collectors.toList());
    }

    public boolean isBookLoanedToUser(Long bookId, String username) {
        return loanRepository.existsByBook_IdAndUser_UsernameAndReturnedFalse(bookId, username);
    }



}
