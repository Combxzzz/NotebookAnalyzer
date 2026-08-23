package com.notebookanalyzer.api.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "notebook_collections")
public class NotebookCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notebook_id", nullable = false)
    private Notebook notebook;

    @Column(name = "received_at", insertable = false, updatable = false)
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

    protected NotebookCollection() {
    }

    public Long getId() {
        return id;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public String getCpuArchitecture() {
        return cpuArchitecture;
    }

    public void setCpuArchitecture(String cpuArchitecture) {
        this.cpuArchitecture = cpuArchitecture;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Integer getCpuThreads() {
        return cpuThreads;
    }

    public void setCpuThreads(Integer cpuThreads) {
        this.cpuThreads = cpuThreads;
    }

    public Integer getCpuTemperatureCelsius() {
        return cpuTemperatureCelsius;
    }

    public void setCpuTemperatureCelsius(Integer cpuTemperatureCelsius) {
        this.cpuTemperatureCelsius = cpuTemperatureCelsius;
    }

    public Integer getMemoryTotalGb() {
        return memoryTotalGb;
    }

    public void setMemoryTotalGb(Integer memoryTotalGb) {
        this.memoryTotalGb = memoryTotalGb;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public Integer getMemorySpeedMhz() {
        return memorySpeedMhz;
    }

    public void setMemorySpeedMhz(Integer memorySpeedMhz) {
        this.memorySpeedMhz = memorySpeedMhz;
    }

    public String getGpuModel() {
        return gpuModel;
    }

    public void setGpuModel(String gpuModel) {
        this.gpuModel = gpuModel;
    }

    public String getStorageModel() {
        return storageModel;
    }

    public void setStorageModel(String storageModel) {
        this.storageModel = storageModel;
    }

    public String getStorageSerialNumber() {
        return storageSerialNumber;
    }

    public void setStorageSerialNumber(String storageSerialNumber) {
        this.storageSerialNumber = storageSerialNumber;
    }

    public String getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(String storageSize) {
        this.storageSize = storageSize;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStorageHealthStatus() {
        return storageHealthStatus;
    }

    public void setStorageHealthStatus(String storageHealthStatus) {
        this.storageHealthStatus = storageHealthStatus;
    }

    public Long getStoragePowerOnHours() {
        return storagePowerOnHours;
    }

    public void setStoragePowerOnHours(Long storagePowerOnHours) {
        this.storagePowerOnHours = storagePowerOnHours;
    }

    public Short getBatteryHealthPercentage() {
        return batteryHealthPercentage;
    }

    public void setBatteryHealthPercentage(Short batteryHealthPercentage) {
        this.batteryHealthPercentage = batteryHealthPercentage;
    }

    public Integer getBatteryCycleCount() {
        return batteryCycleCount;
    }

    public void setBatteryCycleCount(Integer batteryCycleCount) {
        this.batteryCycleCount = batteryCycleCount;
    }

    public Long getBatteryFullCapacity() {
        return batteryFullCapacity;
    }

    public void setBatteryFullCapacity(Long batteryFullCapacity) {
        this.batteryFullCapacity = batteryFullCapacity;
    }

    public Long getBatteryDesignCapacity() {
        return batteryDesignCapacity;
    }

    public void setBatteryDesignCapacity(Long batteryDesignCapacity) {
        this.batteryDesignCapacity = batteryDesignCapacity;
    }

    public String getBatteryCapacityUnit() {
        return batteryCapacityUnit;
    }

    public void setBatteryCapacityUnit(String batteryCapacityUnit) {
        this.batteryCapacityUnit = batteryCapacityUnit;
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
