package com.ismaelebonaventura.home_service.dto;

import java.util.List;
import java.util.UUID;

public record UserHomeAssignmentsResponse(
        List<UserHomeAssignment> heads,
        List<UserHomeAssignment> members
) {
    public record UserHomeAssignment(UUID userId, Integer homeId) {}
}