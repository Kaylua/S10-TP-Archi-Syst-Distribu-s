package com.esiea.library.application;

import com.esiea.library.domain.Book;
import com.esiea.library.domain.BusinessException;
import com.esiea.library.domain.Loan;
import com.esiea.library.domain.LoanStatus;
import com.esiea.library.repository.BookRepository;
import com.esiea.library.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    LoanRepository loanRepository;

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    LoanApplicationService service;

    @Test
    void borrow_creerUnPret_quandLivreDisponible() {
        Book book = availableBook(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Loan loan = service.borrow(1L, "etudiant42");

        assertThat(loan.getStudentId()).isEqualTo("etudiant42");
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(loan.getBorrowedAt()).isNotNull();
        assertThat(book.isAvailable()).isFalse();
    }

    @Test
    void borrow_lanceException_quandLivreIndisponible() {
        Book book = availableBook(1L);
        book.setAvailable(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> service.borrow(1L, "etudiant42"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("disponible");
    }

    @Test
    void borrow_lanceException_quandLivreInexistant() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.borrow(99L, "etudiant42"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void returnBook_clotureLePret_etRendLivreDisponible() {
        Book book = availableBook(1L);
        book.setAvailable(false);
        Loan loan = activeLoan(1L, book);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Loan result = service.returnBook(1L);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isNotNull();
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    void returnBook_lanceException_quandPretDejaRetourne() {
        Loan loan = activeLoan(1L, availableBook(1L));
        loan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> service.returnBook(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("clôturé");
    }

    @Test
    void returnBook_lanceException_quandPretInexistant() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.returnBook(99L))
                .isInstanceOf(BusinessException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Book availableBook(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle("Clean Code");
        book.setAvailable(true);
        return book;
    }

    private Loan activeLoan(Long id, Book book) {
        Loan loan = new Loan();
        loan.setId(id);
        loan.setBook(book);
        loan.setStudentId("etudiant42");
        loan.setStatus(LoanStatus.ACTIVE);
        return loan;
    }
}
