package com.esiea.library.loan.web;

import com.esiea.library.loan.application.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrow(@RequestBody @Valid CreateLoanRequest request) {
        return LoanResponse.from(loanService.borrow(request.bookId(), request.studentId()));
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {
        return LoanResponse.from(loanService.returnBook(id));
    }

    @GetMapping("/student/{studentId}")
    public List<LoanResponse> getStudentHistory(@PathVariable String studentId) {
        return loanService.getStudentHistory(studentId).stream()
                .map(LoanResponse::from)
                .toList();
    }
}
