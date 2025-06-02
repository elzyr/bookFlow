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
    private final MailerMapper reminderMapper;

    public void sendSingle(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("martynaszy02@gmail.com");
        mailSender.send(message);
    }

    public void sendReturnReminders(List<LoanDto> loans) {

        List<MailReminderDto> mails = reminderMapper.toMailDtoList(loans);

        for (MailReminderDto m : mails) {

            String body = String.format("""
                    Drogi użytkowniku,

                    Przypominamy, że książka "%s" powinna zostać zwrócona do dnia %s.
                    Prosimy o oddanie jej na czas.

                    Zespół BookFlow
                    """, m.getBookTitle(), m.getDueDate());

            sendSingle(m.getTo(), "Przypomnienie o zwrocie książki", body);

            loanService.markReminderSent(
                    loans.stream()
                            .filter(l -> l.getUserEmail().equals(m.getTo())
                                    && l.getTitle().equals(m.getBookTitle()))
                            .findFirst()
                            .map(LoanDto::getId)
                            .orElseThrow()
            );
        }
    }
}
