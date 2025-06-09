package com.bookflow.loan;

import com.bookflow.book.Book;
import com.bookflow.book.BookService;
import com.bookflow.exception.ExtensionNotAllowedException;
import com.bookflow.exception.LoanInvalidException;
import com.bookflow.exception.NotFoundException;
import com.bookflow.user.User;
import com.bookflow.user.UserService;
import jakarta.transaction.Transactional;
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

    private static final int LOAN_DURATION_DAYS = 14;
    private static final int BOOK_UNAVAILABLE = 0;
    private static final int MAXIMUM_LOAN_BOOK_NUMBER = 5;
    private static final float MONEY_TO_PAY_FOR_DAY = (float) 0.5;
    private static final int LOANED_BOOK = 1;
    private static final int RANKING_BOOK = 3;
    private static final int RANKING_BOOK_DURATION = 5;

    public List<LoanHistory> getUserLoans(LoanStatus status, String username) {
        List<LoanHistory> loans = loanRepository.findByUser_UsernameAndStatus(username, status);
        if (loans.isEmpty()) {
            throw new NotFoundException("Nie znaleziono ksiązek.");
        }
        return loans;
    }

    public List<LoanHistory> getUserLoans(String username) {
        List<LoanHistory> loans = loanRepository.findByUser_Username(username);
        if (loans.isEmpty()) {
            throw new NotFoundException("Nie znaleziono ksiązek.");
        }
        return loans;
    }


    public void extendLoan(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono wypożyczenia"));

        if (loan.isExtendedTime()) {
            throw new LoanInvalidException("Książka jest już przedłużona");
        }
        if (loan.getStatus() == LoanStatus.RETURN_ACCEPTED) {
            throw new LoanInvalidException("Nie można przedłużyć zwróconej książki");
        }

        if (loan.getReturnDate().isBefore(LocalDate.now())) {
            throw new ExtensionNotAllowedException("Nie można przedłużyć wypożyczenia po terminie zwrotu");
        }

        loan.setExtendedTime(true);
        loan.setReturnDate(loan.getReturnDate().plusDays(LOAN_DURATION_DAYS / 2));
        loanRepository.save(loan);
    }


    public List<LoanHistory> getLoansByStatus(LoanStatus status) {
        return loanRepository.findAllByStatus(status);
    }

    @Transactional
    public void returnBook(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono wypożyczenia"));

        if (loan.getStatus() == LoanStatus.RETURN_ACCEPTED) {
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
        loan.setStatus(LoanStatus.PENDING_RETURN);
        loanRepository.save(loan);
    }


    @Transactional
    public void confirmLoan(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanInvalidException("Nie znaleziono takiego wypozyczenia"));
        if (loan.getStatus() != LoanStatus.PENDING_LOAN) {
            throw new LoanInvalidException("Książka została już zwrócona albo wypożyczona.");
        }

        Book foundBook = bookService.getById(loan.getBook().getId());
        if (foundBook.getAvailableCopies() < 1) {
            throw new LoanInvalidException("Nie ma już kopii tej książki do wypożyczenia");
        }
        foundBook.setAvailableCopies(foundBook.getAvailableCopies() - LOANED_BOOK);
        bookService.saveBook(foundBook);
        loan.setStatus(LoanStatus.LOAN_ACCEPTED);
        loanRepository.save(loan);
    }

    @Transactional
    public void confirmReturn(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanInvalidException("Nie znaleziono takiego wypozyczenia"));
        if (loan.getStatus() != LoanStatus.PENDING_RETURN) {
            throw new LoanInvalidException("Rezerwacja nie oczekuje na zwrot");
        }
        Book foundBook = bookService.getById(loan.getBook().getId());
        foundBook.setAvailableCopies(foundBook.getAvailableCopies() + LOANED_BOOK);
        bookService.saveBook(foundBook);

        loan.setStatus(LoanStatus.RETURN_ACCEPTED);
        loanRepository.save(loan);
    }

    public void cancelReservation(Long loanId) {
        LoanHistory loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanInvalidException("Nie znaleziono takiego wypozyczenia"));
        if (loan.getStatus() != LoanStatus.PENDING_LOAN) {
            throw new LoanInvalidException("Rezerwacja nie oczekuje na wypożyczenie");
        }
        loan.setStatus(LoanStatus.CANCELED);
        loanRepository.save(loan);
    }


    @Transactional
    public void reserveBook(Long bookId, String username) {
        Book book = bookService.getById(bookId);

        if (book.getAvailableCopies() == BOOK_UNAVAILABLE) {
            throw new LoanInvalidException("Wszystkie kopie danej książki są już wypożyczone");
        }

        User user = userService.findByUsername(username);

        List<LoanStatus> excludedStatuses = List.of(LoanStatus.CANCELED, LoanStatus.RETURN_ACCEPTED);

        List<LoanHistory> loans = loanRepository.findByUser_UsernameAndStatusNotIn(username, excludedStatuses);
        boolean isBorrowed = loans.stream()
                .anyMatch(l -> l.getBook().getId().equals(bookId));

        if (isBorrowed) {
            throw new LoanInvalidException("Książka jest już wypożyczona");
        }

        boolean hasExpired = loans.stream()
                .filter(l -> !(l.getStatus() == LoanStatus.RETURN_ACCEPTED))
                .anyMatch(l -> l.getReturnDate().isBefore(LocalDate.now()));

        if (hasExpired) {
            throw new LoanInvalidException("Masz niezwrócone ksiązki po terminie. Nie można wypożyczyć nowej");
        }


        if (loans.size() >= MAXIMUM_LOAN_BOOK_NUMBER) {
            throw new LoanInvalidException("Masz maksymalną ilość wypożyczonych książek (" + MAXIMUM_LOAN_BOOK_NUMBER + ")");
        }

        //save loan
        LocalDate now = LocalDate.now();
        LocalDate returnDate = now.plusDays(LOAN_DURATION_DAYS);

        LoanHistory loan = new LoanHistory();
        loan.setReturnDate(returnDate);
        loan.setStatus(LoanStatus.PENDING_LOAN);
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(now);
        loan.setExtendedTime(false);
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

    public List<BookLoanRankDto> getAverageLoanedTimeFromDate(String fromDate) {
        LocalDate from = LocalDate.parse(fromDate);

        List<LoanHistory> returnedBooks = loanRepository.findAllByStatus(LoanStatus.RETURN_ACCEPTED);

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
        return (loanRepository.existsByBook_IdAndUser_UsernameAndStatusIsNot(bookId, username, LoanStatus.RETURN_ACCEPTED)
                && loanRepository.existsByBook_IdAndUser_UsernameAndStatusIsNot(bookId, username, LoanStatus.CANCELED));
    }

    public double getTotalDeptForUser(String username) {
        List<LoanHistory> loans = loanRepository.findByUser_Username(username);
        return loans.stream()
                .mapToDouble(LoanHistory::getDept)
                .sum();
    }

    public List<LoanHistory> getLoansToBeReturnedSoon(int daysBefore) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(daysBefore);

        return loanRepository
                .findAllByStatus(LoanStatus.LOAN_ACCEPTED).stream()
                .filter(l -> !l.isReminderSent())
                .filter(l -> l.getReturnDate() != null
                        && !l.getReturnDate().isBefore(today)
                        && !l.getReturnDate().isAfter(threshold))
                .toList();
    }

    @Transactional
    public void markReminderSent(Long loanId) {
        loanRepository.findById(loanId).ifPresent(loan -> {
            loan.setReminderSent(true);
            loanRepository.save(loan);
        });
    }

}
