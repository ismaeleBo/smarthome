package com.ismaelebonaventura.home_service.dto;

import java.util.List;
import java.util.UUID;

public record AssignAnalystHomesRequest(
        UUID analystUserId,
        List<Integer> homeIds
) {}