package com.esiea.library.loan.application;

import com.esiea.library.loan.client.AvailabilityResponse;
import com.esiea.library.loan.client.CatalogClient;
import com.esiea.library.loan.domain.BusinessException;
import com.esiea.library.loan.domain.Loan;
import com.esiea.library.loan.domain.LoanStatus;
import com.esiea.library.loan.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    LoanRepository loanRepository;

    @Mock
    CatalogClient catalogClient;

    @InjectMocks
    LoanService loanService;

    @Test
    void borrow_livre_disponible_cree_le_pret() {
        when(catalogClient.getAvailability(1L))
                .thenReturn(new AvailabilityResponse(true, "Clean Code"));
        doNothing().when(catalogClient).markAsBorrowed(1L);

        Loan saved = new Loan();
        saved.setId(10L);
        saved.setBookId(1L);
        saved.setBookTitle("Clean Code");
        saved.setStudentId("etu-01");
        saved.setBorrowedAt(LocalDate.now());
        saved.setStatus(LoanStatus.ACTIVE);
        when(loanRepository.save(any())).thenReturn(saved);

        Loan result = loanService.borrow(1L, "etu-01");

        assertThat(result.getBookTitle()).isEqualTo("Clean Code");
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        verify(catalogClient).markAsBorrowed(1L);
    }

    @Test
    void borrow_livre_non_disponible_leve_exception() {
        when(catalogClient.getAvailability(1L))
                .thenReturn(new AvailabilityResponse(false, "Clean Code"));

        assertThatThrownBy(() -> loanService.borrow(1L, "etu-01"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("disponible");

        verify(catalogClient, never()).markAsBorrowed(any());
    }

    @Test
    void returnBook_pret_actif_cloture_le_pret() {
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setBookId(1L);
        loan.setStatus(LoanStatus.ACTIVE);
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        doNothing().when(catalogClient).markAsReturned(1L);

        Loan result = loanService.returnBook(5L);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isEqualTo(LocalDate.now());
        verify(catalogClient).markAsReturned(1L);
    }

    @Test
    void returnBook_deja_retourne_leve_exception() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnBook(5L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("clôturé");
    }

    @Test
    void returnBook_pret_introuvable_leve_exception() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnBook(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getStudentHistory_retourne_les_prets_de_letudiant() {
        Loan loan = new Loan();
        loan.setStudentId("etu-01");
        when(loanRepository.findByStudentId("etu-01")).thenReturn(List.of(loan));

        assertThat(loanService.getStudentHistory("etu-01")).hasSize(1);
    }
}
