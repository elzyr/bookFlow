package com.bookflow.loan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookLoanRankDto {

    private String title;
    private Double loanCount;

    public BookLoanRankDto(String title, Number loanCount) {
        this.title = title;
        this.loanCount = loanCount.doubleValue();
    }
}
