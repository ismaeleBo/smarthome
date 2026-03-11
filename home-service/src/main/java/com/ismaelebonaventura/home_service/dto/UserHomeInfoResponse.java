package com.ismaelebonaventura.home_service.dto;

import com.ismaelebonaventura.home_service.model.HomeStatus;

public record UserHomeInfoResponse(
        Integer homeId,
        HomeStatus status,
        Double pricePerKwh) {
}