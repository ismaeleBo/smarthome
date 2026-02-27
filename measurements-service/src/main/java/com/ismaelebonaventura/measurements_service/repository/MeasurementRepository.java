package com.ismaelebonaventura.measurements_service.repository;

import com.ismaelebonaventura.measurements_service.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query("select distinct m.homeId from Measurement m order by m.homeId")
    List<Integer> findDistinctHomeIds();

    List<Measurement> findByHomeIdAndMeasurementTimeBetween(
            Integer homeId,
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("select min(m.measurementTime) from Measurement m where m.homeId = :homeId")
    LocalDateTime findMinTimeByHomeId(Integer homeId);

    @Query("select max(m.measurementTime) from Measurement m where m.homeId = :homeId")
    LocalDateTime findMaxTimeByHomeId(Integer homeId);
}