package com.esiea.library.web;

import com.esiea.library.domain.Loan;
import com.esiea.library.domain.LoanStatus;

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
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getStudentId(),
                loan.getBorrowedAt(),
                loan.getReturnedAt(),
                loan.getStatus()
        );
    }
}
