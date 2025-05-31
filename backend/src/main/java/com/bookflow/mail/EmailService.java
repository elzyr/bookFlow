package com.bookflow.mail;
import com.bookflow.loan.LoanDto;
import com.bookflow.loan.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final LoanService loanService;

    public void sendSingle(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("martynaszy02@gmail.com");
        mailSender.send(message);
    }

    public void sendReturnReminders(List<LoanDto> loans) {
        for (LoanDto dto : loans) {
            String content = String.format("""
                Drogi użytkowniku,

                Przypominamy, że książka "%s" powinna zostać zwrócona do dnia %s.
                Prosimy o oddanie jej na czas.

                Zespół bookFlow
                """, dto.getTitle(), dto.getReturnDate());

            sendSingle(dto.getUserEmail(),
                    "Przypomnienie o zwrocie książki",
                    content);

            loanService.markReminderSent(dto.getId());
        }
    }

}
