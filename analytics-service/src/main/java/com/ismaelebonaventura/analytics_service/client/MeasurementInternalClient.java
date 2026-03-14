package com.ismaelebonaventura.analytics_service.client;

import com.ismaelebonaventura.analytics_service.remote.RemoteMeasurementResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MeasurementInternalClient {

    private final WebClient measurementsWebClient;

    @Retry(name = "measurementsService")
    @CircuitBreaker(name = "measurementsService")
    public List<RemoteMeasurementResponse> getMeasurements(
            Integer homeId,
            LocalDateTime from,
            LocalDateTime to,
            String applianceType) {
        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to are required");
        }

        List<RemoteMeasurementResponse> response = measurementsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/homes/{homeId}/measurements")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .queryParamIfPresent("applianceType", Optional.ofNullable(applianceType))
                        .build(homeId))
                .retrieve()
                .bodyToFlux(RemoteMeasurementResponse.class)
                .collectList()
                .block();

        return response != null ? response : Collections.emptyList();
    }
}
