package com.ismaelebonaventura.measurements_service.dto;

import java.util.List;
import java.util.UUID;

public record RemoteUserHomesResponse(
        UUID userId,
        List<RemoteUserHomeInfoResponse> homes) {
}
