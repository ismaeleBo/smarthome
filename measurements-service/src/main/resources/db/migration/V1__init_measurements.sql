-- ============================
-- Measurements main table
-- ============================

CREATE TABLE measurements (
    id BIGSERIAL PRIMARY KEY,

    home_id INTEGER NOT NULL,
    appliance_type VARCHAR(64) NOT NULL,

    energy_consumption_kwh DOUBLE PRECISION NOT NULL,

    measurement_time TIMESTAMP NOT NULL,

    outdoor_temperature_c DOUBLE PRECISION,
    season VARCHAR(16),
    household_size INTEGER
);

-- ============================
-- Natural key constraint
-- ============================

ALTER TABLE measurements
ADD CONSTRAINT uq_measurements_natural_key
UNIQUE (home_id, appliance_type, measurement_time);

CREATE INDEX idx_measurements_home_time
ON measurements(home_id, measurement_time);

CREATE INDEX idx_measurements_home_appliance_time
ON measurements(home_id, appliance_type, measurement_time);

-- ============================
-- Dataset import tracking
-- ============================

CREATE TABLE dataset_import (
    dataset_key VARCHAR(64) PRIMARY KEY,
    imported_at TIMESTAMP NOT NULL,
    row_count BIGINT NOT NULL
);