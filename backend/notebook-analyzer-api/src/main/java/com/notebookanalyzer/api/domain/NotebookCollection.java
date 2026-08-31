package com.notebookanalyzer.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "notebook_collections")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class NotebookCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notebook_id", nullable = false)
    private Notebook notebook;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Column(name = "cpu_model")
    private String cpuModel;

    @Column(name = "cpu_architecture")
    private String cpuArchitecture;

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column(name = "cpu_threads")
    private Integer cpuThreads;

    @Column(name = "cpu_temperature_celsius")
    private Integer cpuTemperatureCelsius;

    @Column(name = "memory_total_gb")
    private Integer memoryTotalGb;

    @Column(name = "memory_type")
    private String memoryType;

    @Column(name = "memory_speed_mhz")
    private Integer memorySpeedMhz;

    @Column(name = "gpu_model")
    private String gpuModel;

    @Column(name = "storage_model")
    private String storageModel;

    @Column(name = "storage_serial_number")
    private String storageSerialNumber;

    @Column(name = "storage_size")
    private String storageSize;

    @Column(name = "storage_type")
    private String storageType;

    @Column(name = "storage_health_status")
    private String storageHealthStatus;

    @Column(name = "storage_power_on_hours")
    private Long storagePowerOnHours;

    @Column(name = "battery_health_percentage")
    private Short batteryHealthPercentage;

    @Column(name = "battery_cycle_count")
    private Integer batteryCycleCount;

    @Column(name = "battery_full_capacity")
    private Long batteryFullCapacity;

    @Column(name = "battery_design_capacity")
    private Long batteryDesignCapacity;

    @Column(name = "battery_capacity_unit")
    private String batteryCapacityUnit;

    @PrePersist
    private void onCreate() {
        if (this.receivedAt == null) {
            this.receivedAt = Instant.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotebookCollection that = (NotebookCollection) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
