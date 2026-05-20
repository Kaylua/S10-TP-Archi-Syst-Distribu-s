package com.esiea.library.loan.application;

import com.esiea.library.loan.client.AvailabilityResponse;
import com.esiea.library.loan.client.CatalogClient;
import com.esiea.library.loan.domain.BusinessException;
import com.esiea.library.loan.domain.Loan;
import com.esiea.library.loan.domain.LoanStatus;
import com.esiea.library.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CatalogClient catalogClient;

    @Transactional
    public Loan borrow(Long bookId, String studentId) {
        AvailabilityResponse avail = catalogClient.getAvailability(bookId);

        if (!avail.available()) {
            throw new BusinessException("Ce livre n'est pas disponible à l'emprunt");
        }

        catalogClient.markAsBorrowed(bookId);

        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setBookTitle(avail.title());
        loan.setStudentId(studentId);
        loan.setBorrowedAt(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new BusinessException("Prêt introuvable : " + loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BusinessException("Ce prêt est déjà clôturé");
        }

        loan.setReturnedAt(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        catalogClient.markAsReturned(loan.getBookId());
        return loan;
    }

    public List<Loan> getStudentHistory(String studentId) {
        return loanRepository.findByStudentId(studentId);
    }
}
