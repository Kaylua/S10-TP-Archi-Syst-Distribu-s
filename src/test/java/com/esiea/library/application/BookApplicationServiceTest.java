package com.esiea.library.application;

import com.esiea.library.domain.Book;
import com.esiea.library.domain.BusinessException;
import com.esiea.library.repository.BookRepository;
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
class BookApplicationServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookApplicationService service;

    @Test
    void getById_retourneLeLivre_quandIlExiste() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = service.getById(1L);

        assertThat(result.getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getById_lanceException_quandLivreInexistant() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("99");
    }

    @Test
    void add_sauvegardeLeLivre_avecDisponibiliteTrue() {
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = service.add("Clean Code", "Robert Martin", "978-0132350884");

        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.isAvailable()).isTrue();
    }
}
