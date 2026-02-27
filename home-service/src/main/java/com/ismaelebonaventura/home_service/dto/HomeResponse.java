package com.ismaelebonaventura.home_service.dto;

import com.ismaelebonaventura.home_service.model.HomeStatus;

import java.util.UUID;

public record HomeResponse(
        Integer homeId,
        HomeStatus status,
        String address,
        String streetNumber,
        String city,
        Double pricePerKwh,
        UUID headUserId
) {}
