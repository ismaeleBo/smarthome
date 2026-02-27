package com.ismaelebonaventura.measurements_service.dto;

import java.time.LocalDateTime;

public record CoverageResponse(LocalDateTime from, LocalDateTime to) {}
