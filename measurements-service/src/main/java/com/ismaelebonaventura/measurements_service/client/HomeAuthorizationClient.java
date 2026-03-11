package com.ismaelebonaventura.measurements_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ismaelebonaventura.measurements_service.dto.RemoteUserHomesResponse;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HomeAuthorizationClient {

    private final WebClient homeWebClient;

    public RemoteUserHomesResponse getUserHomes(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        RemoteUserHomesResponse response = homeWebClient.get()
                .uri("/internal/users/{userId}/homes", userId)
                .retrieve()
                .bodyToMono(RemoteUserHomesResponse.class)
                .block();

        if (response == null) {
            return new RemoteUserHomesResponse(userId, Collections.emptyList());
        }

        return response;
    }

    public boolean canAccessHome(UUID userId, Integer homeId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }

        return getUserHomes(userId).homes().stream()
                .anyMatch(h -> homeId.equals(h.homeId()));
    }

    public Set<Integer> getAccessibleHomeIds(UUID userId) {
        return getUserHomes(userId).homes().stream()
                .map(h -> h.homeId())
                .collect(Collectors.toSet());
    }
}
