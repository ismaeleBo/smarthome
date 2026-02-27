package com.ismaelebonaventura.measurements_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ismaelebonaventura.measurements_service.config.JwtProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class MeasurementsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeasurementsServiceApplication.class, args);
	}

}
