package com.bookflow.mail;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;

@Value
@Builder
public class MailReminderDto {
    String to;
    String bookTitle;
    LocalDate dueDate;
}
