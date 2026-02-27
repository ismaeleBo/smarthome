package com.ismaelebonaventura.measurements_service.importer;

import com.ismaelebonaventura.measurements_service.aop.Audited;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvImportService {
    private final JdbcTemplate jdbcTemplate;

    @Audited(action = "IMPORT_DATASET")
    @Transactional
    public long importDataset(InputStream inputStream) throws Exception {

        String datasetKey = "kaggle-2023-v1";

        Long already = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dataset_import WHERE dataset_key = ?",
                Long.class,
                datasetKey
        );

        if (already != null && already > 0) {
            return 0; // già importato
        }

        String sql = """
        INSERT INTO measurements
        (home_id, appliance_type, energy_consumption_kwh, measurement_time,
         outdoor_temperature_c, season, household_size)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (home_id, appliance_type, measurement_time) DO NOTHING
        """;

        long total = 0;
        List<Object[]> batch = new ArrayList<>(2000);

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) continue;

                List<String> cols = splitCsvLine(line);

                Integer homeId = Integer.parseInt(cols.get(0).trim());
                String applianceType = cols.get(1).trim();
                Double consumption = Double.parseDouble(cols.get(2).trim());

                LocalTime time = LocalTime.parse(cols.get(3).trim(), DateTimeFormatter.ofPattern("H:mm"));
                LocalDate date = LocalDate.parse(cols.get(4).trim());
                LocalDateTime measurementTime = LocalDateTime.of(date, time);

                Double outdoorTemp = parseNullableDouble(cols.get(5));
                String season = emptyToNull(cols.get(6));
                Integer householdSize = parseNullableInt(cols.get(7));

                batch.add(new Object[]{
                        homeId,
                        applianceType,
                        consumption,
                        measurementTime,
                        outdoorTemp,
                        season,
                        householdSize
                });

                if (batch.size() >= 2000) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    total += batch.size();
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batch);
            total += batch.size();
        }

        jdbcTemplate.update(
                "INSERT INTO dataset_import(dataset_key, imported_at, row_count) VALUES (?, now(), ?)",
                datasetKey,
                total
        );

        return total;
    }

    private static Double parseNullableDouble(String s) {
        if (s == null) return null;
        String v = s.trim();
        if (v.isEmpty()) return null;
        return Double.parseDouble(v);
    }

    private static Integer parseNullableInt(String s) {
        if (s == null) return null;
        String v = s.trim();
        if (v.isEmpty()) return null;
        return Integer.parseInt(v);
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String v = s.trim();
        return v.isEmpty() ? null : v;
    }

    private static List<String> splitCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }

        out.add(cur.toString());
        return out;
    }
}