package com.bookflow.loan;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class LoanCleanupService {

    private final LoanRepository loanRepository;

    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void cancelStalePendingLoans() {
        LocalDate cutoff = LocalDate.now().minusDays(3);
        var stale = loanRepository.findByStatusAndBorrowDateBefore(
                LoanStatus.PENDING_LOAN, cutoff);
        stale.forEach(lh -> lh.setStatus(LoanStatus.CANCELED));
        loanRepository.saveAll(stale);
    }
}