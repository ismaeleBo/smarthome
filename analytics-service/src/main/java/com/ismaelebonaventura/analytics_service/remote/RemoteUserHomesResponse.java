package com.ismaelebonaventura.analytics_service.remote;

import java.util.List;
import java.util.UUID;

public record RemoteUserHomesResponse(
        UUID userId,
        List<RemoteUserHomeInfoResponse> homes) {
}
