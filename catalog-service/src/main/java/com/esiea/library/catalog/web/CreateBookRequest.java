package com.esiea.library.catalog.web;

import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @NotBlank String title,
        @NotBlank String author,
        String isbn
) {}
