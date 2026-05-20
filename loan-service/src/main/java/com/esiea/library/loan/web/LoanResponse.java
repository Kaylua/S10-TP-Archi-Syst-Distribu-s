package com.esiea.library.loan.web;

import com.esiea.library.loan.domain.Loan;
import com.esiea.library.loan.domain.LoanStatus;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String studentId,
        LocalDate borrowedAt,
        LocalDate returnedAt,
        LoanStatus status
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBookId(),
                loan.getBookTitle(),
                loan.getStudentId(),
                loan.getBorrowedAt(),
                loan.getReturnedAt(),
                loan.getStatus()
        );
    }
}
