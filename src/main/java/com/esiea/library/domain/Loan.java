package com.esiea.library.domain;

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

    @ManyToOne(optional = false)
    private Book book;

    private String studentId;
    private LocalDate borrowedAt;
    private LocalDate returnedAt;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;
}
