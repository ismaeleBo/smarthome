package com.ismaelebonaventura.analytics_service.client;

import com.ismaelebonaventura.analytics_service.remote.RemoteUserHomesResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HomeInternalClient {

    private final WebClient homeWebClient;

    @Retry(name = "homeService")
    @CircuitBreaker(name = "homeService")
    public RemoteUserHomesResponse getUserHomes(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        RemoteUserHomesResponse response = homeWebClient.get()
                .uri("/internal/users/{userId}/homes", userId)
                .retrieve()
                .bodyToMono(RemoteUserHomesResponse.class)
                .block();

        return response != null
                ? response
                : new RemoteUserHomesResponse(userId, Collections.emptyList());
    }
}
