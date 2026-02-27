package com.ismaelebonaventura.measurements_service.controller;

import com.ismaelebonaventura.measurements_service.dto.CoverageResponse;
import com.ismaelebonaventura.measurements_service.model.Measurement;
import com.ismaelebonaventura.measurements_service.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementRepository repository;

    @GetMapping("/homes")
    public List<Integer> getHomeIds() {
        return repository.findDistinctHomeIds();
    }

    @GetMapping("/homes/{homeId}/coverage")
    public CoverageResponse getCoverage(@PathVariable Integer homeId) {
        return new CoverageResponse(
                repository.findMinTimeByHomeId(homeId),
                repository.findMaxTimeByHomeId(homeId)
        );
    }

    @GetMapping
    public List<Measurement> getMeasurements(
            @RequestParam Integer homeId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {

        return repository.findByHomeIdAndMeasurementTimeBetween(homeId, from, to);
    }
}