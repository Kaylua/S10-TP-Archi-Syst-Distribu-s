package com.esiea.library.loan.repository;

import com.esiea.library.loan.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStudentId(String studentId);
}
