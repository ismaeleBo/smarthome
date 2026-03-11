package com.ismaelebonaventura.measurements_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_id", nullable = false)
    private Integer homeId;

    @Column(name = "appliance_type", nullable = false, length = 64)
    private String applianceType;

    @Column(name = "energy_consumption_kwh", nullable = false)
    private Double energyConsumptionKwh;

    @Column(name = "measurement_time", nullable = false)
    private LocalDateTime measurementTime;

    @Column(name = "outdoor_temperature_c")
    private Double outdoorTemperatureC;

    @Column(name = "season", length = 16)
    private String season;

    @Column(name = "household_size")
    private Integer householdSize;
}