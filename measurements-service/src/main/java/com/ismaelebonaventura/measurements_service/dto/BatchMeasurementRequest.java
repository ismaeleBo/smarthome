package com.ismaelebonaventura.measurements_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BatchMeasurementRequest(
    List<Integer> homeIds,
    LocalDateTime from,
    LocalDateTime to,
    String applianceType
) {}
