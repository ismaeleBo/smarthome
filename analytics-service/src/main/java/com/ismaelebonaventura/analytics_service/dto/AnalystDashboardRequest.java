package com.ismaelebonaventura.analytics_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AnalystDashboardRequest(
        List<Integer> homeIds,
        LocalDateTime from,
        LocalDateTime to,
        String applianceType) {
}
