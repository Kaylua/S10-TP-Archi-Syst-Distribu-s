package com.esiea.library.catalog.application;

import com.esiea.library.catalog.domain.Book;
import com.esiea.library.catalog.domain.BusinessException;
import com.esiea.library.catalog.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookService bookService;

    @Test
    void listAll_retourne_tous_les_livres() {
        Book b = new Book();
        b.setTitle("Clean Code");
        when(bookRepository.findAll()).thenReturn(List.of(b));

        assertThat(bookService.listAll()).hasSize(1);
    }

    @Test
    void getById_livre_inexistant_leve_exception() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("99");
    }

    @Test
    void markAsBorrowed_rend_livre_indisponible() {
        Book book = new Book();
        book.setAvailable(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        bookService.markAsBorrowed(1L);

        assertThat(book.isAvailable()).isFalse();
    }

    @Test
    void markAsBorrowed_deja_emprunte_leve_exception() {
        Book book = new Book();
        book.setAvailable(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.markAsBorrowed(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void markAsReturned_rend_livre_disponible() {
        Book book = new Book();
        book.setAvailable(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        bookService.markAsReturned(1L);

        assertThat(book.isAvailable()).isTrue();
    }
}
