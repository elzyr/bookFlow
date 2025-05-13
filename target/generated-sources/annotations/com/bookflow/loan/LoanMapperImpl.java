package com.bookflow.loan;

import com.bookflow.book.Book;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-13T20:33:26+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class LoanMapperImpl implements LoanMapper {

    @Override
    public LoanDto toDto(LoanHistory loan) {
        if ( loan == null ) {
            return null;
        }

        LoanDto.LoanDtoBuilder loanDto = LoanDto.builder();

        if ( loan.getLoanId() != null ) {
            loanDto.id( loan.getLoanId().intValue() );
        }
        loanDto.bookId( loanBookId( loan ) );
        loanDto.title( loanBookTitle( loan ) );
        loanDto.borrowDate( loan.getBorrowDate() );
        loanDto.returnDate( loan.getReturnDate() );
        loanDto.extendedTime( loan.isExtendedTime() );
        loanDto.returned( loan.isReturned() );
        loanDto.bookReturned( loan.getBookReturned() );
        loanDto.dept( loan.getDept() );

        return loanDto.build();
    }

    @Override
    public List<LoanDto> toDtoList(List<LoanHistory> loans) {
        if ( loans == null ) {
            return null;
        }

        List<LoanDto> list = new ArrayList<LoanDto>( loans.size() );
        for ( LoanHistory loanHistory : loans ) {
            list.add( toDto( loanHistory ) );
        }

        return list;
    }

    private Long loanBookId(LoanHistory loanHistory) {
        if ( loanHistory == null ) {
            return null;
        }
        Book book = loanHistory.getBook();
        if ( book == null ) {
            return null;
        }
        Long id = book.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String loanBookTitle(LoanHistory loanHistory) {
        if ( loanHistory == null ) {
            return null;
        }
        Book book = loanHistory.getBook();
        if ( book == null ) {
            return null;
        }
        String title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
