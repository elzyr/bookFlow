package com.bookflow.mail;

import com.bookflow.loan.LoanDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MailerMapper {

    @Mapping(source = "userEmail", target = "to")
    @Mapping(source = "title",      target = "bookTitle")
    @Mapping(source = "returnDate", target = "dueDate")
    MailReminderDto toMailDto(LoanDto loan);

    List<MailReminderDto> toMailDtoList(List<LoanDto> loans);
}
