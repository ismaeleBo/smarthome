package com.ismaelebonaventura.measurements_service.dto;

import java.util.List;

public record AnalystHomesRequest(
        List<Integer> homeIds) {
}
