package com.esiea.library.web;

import com.esiea.library.domain.Book;

public record BookResponse(Long id, String title, String author, String isbn, boolean available) {

    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.isAvailable());
    }
}
