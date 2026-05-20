package com.esiea.library.catalog.web;

import com.esiea.library.catalog.application.BookService;
import com.esiea.library.catalog.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// Endpoints internes réservés aux appels inter-services (non exposés via la gateway)
@RestController
@RequestMapping("/internal/books")
@RequiredArgsConstructor
public class InternalBookController {

    private final BookService bookService;

    @GetMapping("/{id}/availability")
    public AvailabilityResponse getAvailability(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return new AvailabilityResponse(book.isAvailable(), book.getTitle());
    }

    @PatchMapping("/{id}/borrow")
    public void markAsBorrowed(@PathVariable Long id) {
        bookService.markAsBorrowed(id);
    }

    @PatchMapping("/{id}/return")
    public void markAsReturned(@PathVariable Long id) {
        bookService.markAsReturned(id);
    }
}
