package com.esiea.library.loan.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence au livre dans catalog-service (pas de FK cross-service)
    private Long bookId;
    private String bookTitle;

    private String studentId;
    private LocalDate borrowedAt;
    private LocalDate returnedAt;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;
}
