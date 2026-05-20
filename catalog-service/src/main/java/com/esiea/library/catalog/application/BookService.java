package com.esiea.library.catalog.application;

import com.esiea.library.catalog.domain.Book;
import com.esiea.library.catalog.domain.BusinessException;
import com.esiea.library.catalog.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    public List<Book> search(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Livre introuvable : " + id));
    }

    @Transactional
    public Book add(String title, String author, String isbn) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        return bookRepository.save(book);
    }

    @Transactional
    public void markAsBorrowed(Long id) {
        Book book = getById(id);
        if (!book.isAvailable()) {
            throw new BusinessException("Livre déjà emprunté : " + id);
        }
        book.setAvailable(false);
        bookRepository.save(book);
    }

    @Transactional
    public void markAsReturned(Long id) {
        Book book = getById(id);
        book.setAvailable(true);
        bookRepository.save(book);
    }
}
