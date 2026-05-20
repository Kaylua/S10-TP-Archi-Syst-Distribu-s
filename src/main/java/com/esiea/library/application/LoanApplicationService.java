package com.esiea.library.application;

import com.esiea.library.domain.Book;
import com.esiea.library.domain.BusinessException;
import com.esiea.library.domain.Loan;
import com.esiea.library.domain.LoanStatus;
import com.esiea.library.repository.BookRepository;
import com.esiea.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Loan borrow(Long bookId, String studentId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Livre introuvable : " + bookId));

        if (!book.isAvailable()) {
            throw new BusinessException("Ce livre n'est pas disponible à l'emprunt");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
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
        loan.getBook().setAvailable(true);
        bookRepository.save(loan.getBook());
        return loanRepository.save(loan);
    }

    public List<Loan> getStudentHistory(String studentId) {
        return loanRepository.findByStudentId(studentId);
    }
}
