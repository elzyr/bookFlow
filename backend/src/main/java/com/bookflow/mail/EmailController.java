package com.bookflow.mail;

import com.bookflow.loan.LoanDto;
import com.bookflow.loan.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final LoanService loanService;
    private final static Integer RETURN_DAYS = 3;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> sendReminders() {
        List<LoanDto> loans = loanService.getLoansToBeReturnedSoon(RETURN_DAYS);
        emailService.sendReturnReminders(loans);
        return ResponseEntity.ok().build();
    }

}
