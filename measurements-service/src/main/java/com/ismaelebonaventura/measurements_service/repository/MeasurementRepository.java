package com.ismaelebonaventura.measurements_service.repository;

import com.ismaelebonaventura.measurements_service.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

	@Query("select distinct m.homeId from Measurement m order by m.homeId")
	List<Integer> findDistinctHomeIds();

	@Query("select min(m.measurementTime) from Measurement m where m.homeId = :homeId")
	LocalDateTime findMinTimeByHomeId(Integer homeId);

	@Query("select max(m.measurementTime) from Measurement m where m.homeId = :homeId")
	LocalDateTime findMaxTimeByHomeId(Integer homeId);

	@Query("""
			    select distinct m.applianceType
			    from Measurement m
			    where m.homeId = :homeId
			    order by m.applianceType
			""")
	List<String> findDistinctApplianceTypesByHomeId(@Param("homeId") Integer homeId);

	List<Measurement> findByHomeIdAndMeasurementTimeBetweenOrderByMeasurementTimeAsc(
			Integer homeId,
			LocalDateTime from,
			LocalDateTime to);

	List<Measurement> findByHomeIdAndMeasurementTimeBetweenAndApplianceTypeOrderByMeasurementTimeAsc(
			Integer homeId,
			LocalDateTime from,
			LocalDateTime to,
			String applianceType);

	List<Measurement> findByHomeIdInAndMeasurementTimeBetweenOrderByMeasurementTimeAsc(
			List<Integer> homeIds,
			LocalDateTime from,
			LocalDateTime to);

	List<Measurement> findByHomeIdInAndMeasurementTimeBetweenAndApplianceTypeOrderByMeasurementTimeAsc(
			List<Integer> homeIds,
			LocalDateTime from,
			LocalDateTime to,
			String applianceType);
}