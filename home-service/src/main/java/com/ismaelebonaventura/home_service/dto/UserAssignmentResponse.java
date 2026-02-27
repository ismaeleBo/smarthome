package com.ismaelebonaventura.home_service.dto;

import java.util.List;
import java.util.UUID;

public record UserAssignmentResponse(
        UUID userId,
        boolean assigned,
        AssignmentType type,
        List<Integer> homeIds
) {

    public enum AssignmentType {
        NONE,
        HEAD,
        MEMBER,
        ANALYST
    }

    public static UserAssignmentResponse none(UUID userId) {
        return new UserAssignmentResponse(userId, false, AssignmentType.NONE, List.of());
    }

    public static UserAssignmentResponse head(UUID userId, List<Integer> homeIds) {
        return new UserAssignmentResponse(userId, true, AssignmentType.HEAD, homeIds);
    }

    public static UserAssignmentResponse member(UUID userId, List<Integer> homeIds) {
        return new UserAssignmentResponse(userId, true, AssignmentType.MEMBER, homeIds);
    }

    public static UserAssignmentResponse analyst(UUID userId, List<Integer> homeIds) {
        return new UserAssignmentResponse(userId, true, AssignmentType.ANALYST, homeIds);
    }
}