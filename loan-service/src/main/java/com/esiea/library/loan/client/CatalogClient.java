package com.esiea.library.loan.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", url = "${catalog.url}")
public interface CatalogClient {

    @GetMapping("/internal/books/{id}/availability")
    AvailabilityResponse getAvailability(@PathVariable Long id);

    @PatchMapping("/internal/books/{id}/borrow")
    void markAsBorrowed(@PathVariable Long id);

    @PatchMapping("/internal/books/{id}/return")
    void markAsReturned(@PathVariable Long id);
}
