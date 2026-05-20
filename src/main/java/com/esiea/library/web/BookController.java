package com.esiea.library.web;

import com.esiea.library.application.BookApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookApplicationService bookService;

    @GetMapping
    public List<BookResponse> list(@RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) {
            return bookService.search(q).stream().map(BookResponse::from).toList();
        }
        return bookService.listAll().stream().map(BookResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return BookResponse.from(bookService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse add(@RequestBody @Valid CreateBookRequest request) {
        return BookResponse.from(bookService.add(request.title(), request.author(), request.isbn()));
    }
}
