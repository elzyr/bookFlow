package com.bookflow.loan;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "loanId", target = "id")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "user.email", target = "userEmail")
    LoanDto toDto(LoanHistory loan);

    List<LoanDto> toDtoList(List<LoanHistory> loans);
}
