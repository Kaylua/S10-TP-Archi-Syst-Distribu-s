package com.esiea.library.catalog.web;

// Contrat interne exposé au loan-service
public record AvailabilityResponse(boolean available, String title) {}
