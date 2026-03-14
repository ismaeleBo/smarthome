package com.ismaelebonaventura.analytics_service.remote;

public record RemoteUserHomeInfoResponse(
        Integer homeId,
        String status,
        Double pricePerKwh) {
}
