package com.ismaelebonaventura.analytics_service.remote;

import java.time.LocalDateTime;
import java.util.List;

public record RemoteBatchMeasurementRequest(
    List<Integer> homeIds,
    LocalDateTime from,
    LocalDateTime to,
    String applianceType
) {}
