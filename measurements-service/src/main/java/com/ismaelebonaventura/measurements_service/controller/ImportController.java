package com.ismaelebonaventura.measurements_service.controller;

import com.ismaelebonaventura.measurements_service.importer.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ImportController {

    private final CsvImportService importService;

    @PostMapping("/import")
    public ResponseEntity<String> importCsv() throws Exception {

        var resource = new ClassPathResource("dataset/smart_home_energy_consumption_large.csv");

        long rows = importService.importDataset(resource.getInputStream());

        return ResponseEntity.ok("Imported rows: " + rows);
    }
}