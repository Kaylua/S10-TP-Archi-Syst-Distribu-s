package com.esiea.library.loan.client;

// Miroir du contrat exposé par catalog-service sur /internal/books/{id}/availability
public record AvailabilityResponse(boolean available, String title) {}
