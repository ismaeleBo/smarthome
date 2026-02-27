package com.ismaelebonaventura.home_service.dto;

public record ConfigureHomeRequest(
        String address,
        String streetNumber,
        String city,
        Double pricePerKwh
) {}