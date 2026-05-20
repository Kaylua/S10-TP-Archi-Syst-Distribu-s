package com.esiea.library.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLoanRequest(
        @NotNull Long bookId,
        @NotBlank String studentId
) {}
