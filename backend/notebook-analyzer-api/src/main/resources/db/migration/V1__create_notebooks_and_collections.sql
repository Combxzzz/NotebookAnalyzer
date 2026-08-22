CREATE TABLE notebooks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    serial_number TEXT UNIQUE,
    CONSTRAINT chk_notebook_serial_number
        CHECK (serial_number IS NULL OR serial_number <> 'N/A'),
    manufacturer TEXT,
    model TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notebook_collections (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    notebook_id BIGINT NOT NULL REFERENCES notebooks(id),
    received_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    cpu_model TEXT,
    cpu_architecture TEXT,
    cpu_cores INTEGER,
    cpu_threads INTEGER,
    cpu_temperature_celsius INTEGER,

    memory_total_gb INTEGER,
    memory_type TEXT,
    memory_speed_mhz INTEGER,

    gpu_model TEXT,

    storage_model TEXT,
    storage_serial_number TEXT,
    storage_size TEXT,
    storage_type TEXT,
    storage_health_status TEXT,
    storage_power_on_hours BIGINT,

    battery_health_percentage SMALLINT,
    battery_cycle_count INTEGER,
    battery_full_capacity BIGINT,
    battery_design_capacity BIGINT,
    battery_capacity_unit TEXT,

    CONSTRAINT chk_battery_health
        CHECK (
            battery_health_percentage IS NULL
            OR battery_health_percentage >= 0
            ),
    CONSTRAINT chk_battery_cycles
        CHECK (battery_cycle_count IS NULL OR battery_cycle_count >= 0),
    CONSTRAINT chk_storage_hours
        CHECK (storage_power_on_hours IS NULL OR storage_power_on_hours >= 0)
);

CREATE INDEX idx_notebook_collections_notebook_received_at
    ON notebook_collections (notebook_id, received_at DESC);
