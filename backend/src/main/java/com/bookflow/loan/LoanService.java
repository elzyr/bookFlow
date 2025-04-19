package com.bookflow.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    public List<LoanDto> getLoanedBooks(Long userId, boolean returned) {
        List<LoanHistory> loans = returned
                ? loanRepository.findByUserIdAndReturnedTrue(userId)
                : loanRepository.findByUserIdAndReturnedFalse(userId);

        return loans.stream()
                .map(loan -> LoanDto.builder()
                        .title(loan.getBook().getTitle())
                        .borrowDate(loan.getBorrowDate())
                        .returnDate(loan.getReturnDate())
                        .extendedTime(loan.isExtendedTime())
                        .returned(loan.isReturned())
                        .build())
                .toList();
    }
}
