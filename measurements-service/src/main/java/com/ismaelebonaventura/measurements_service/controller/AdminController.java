package com.ismaelebonaventura.measurements_service.controller;

import com.ismaelebonaventura.measurements_service.importer.CsvImportService;
import com.ismaelebonaventura.measurements_service.repository.MeasurementRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CsvImportService importService;
    private final MeasurementRepository measurementRepository;

    @PostMapping("/import")
    public ResponseEntity<String> importCsv() throws Exception {

        var resource = new ClassPathResource("dataset/smart_home_energy_consumption_large.csv");

        long rows = importService.importDataset(resource.getInputStream());

        return ResponseEntity.ok("Imported rows: " + rows);
    }

    @GetMapping("/homes")
    public List<Integer> getHomeIds() {
        return measurementRepository.findDistinctHomeIds();
    }
}