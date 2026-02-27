package com.ismaelebonaventura.home_service.dto;

import com.ismaelebonaventura.home_service.model.HomeStatus;

public record UserHomeResponse(
        Integer homeId,
        HomeStatus status
) {}