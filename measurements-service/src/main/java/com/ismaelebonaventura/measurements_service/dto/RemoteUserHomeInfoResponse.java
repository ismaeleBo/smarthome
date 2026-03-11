package com.ismaelebonaventura.measurements_service.dto;

public record RemoteUserHomeInfoResponse(
        Integer homeId,
        String status,
        Double pricePerKwh) {
}
