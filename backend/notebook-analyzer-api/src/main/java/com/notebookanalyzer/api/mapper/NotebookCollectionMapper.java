package com.notebookanalyzer.api.mapper;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.domain.NotebookCollection;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionRequestDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionResponseDTO;

public class NotebookCollectionMapper {
    public static NotebookCollectionResponseDTO toResponseDTO(NotebookCollection notebookCollection) {
        return new NotebookCollectionResponseDTO(
                notebookCollection.getId(),
                notebookCollection.getNotebook().getId(),
                notebookCollection.getReceivedAt(),
                notebookCollection.getCpuModel(),
                notebookCollection.getCpuArchitecture(),
                notebookCollection.getCpuCores(),
                notebookCollection.getCpuThreads(),
                notebookCollection.getCpuTemperatureCelsius(),
                notebookCollection.getMemoryTotalGb(),
                notebookCollection.getMemoryType(),
                notebookCollection.getMemorySpeedMhz(),
                notebookCollection.getGpuModel(),
                notebookCollection.getStorageModel(),
                notebookCollection.getStorageSerialNumber(),
                notebookCollection.getStorageSize(),
                notebookCollection.getStorageType(),
                notebookCollection.getStorageHealthStatus(),
                notebookCollection.getStoragePowerOnHours(),
                notebookCollection.getBatteryHealthPercentage(),
                notebookCollection.getBatteryCycleCount(),
                notebookCollection.getBatteryFullCapacity(),
                notebookCollection.getBatteryDesignCapacity(),
                notebookCollection.getBatteryCapacityUnit()
        );
    }

    public static NotebookCollection toEntity(NotebookCollectionRequestDTO dto, Notebook notebook) {
        return new NotebookCollection(
                notebook,
                dto.cpuModel(),
                dto.cpuArchitecture(),
                dto.cpuCores(),
                dto.cpuThreads(),
                dto.cpuTemperatureCelsius(),
                dto.memoryTotalGb(),
                dto.memoryType(),
                dto.memorySpeedMhz(),
                dto.gpuModel(),
                dto.storageModel(),
                dto.storageSerialNumber(),
                dto.storageSize(),
                dto.storageType(),
                dto.storageHealthStatus(),
                dto.storagePowerOnHours(),
                dto.batteryHealthPercentage(),
                dto.batteryCycleCount(),
                dto.batteryFullCapacity(),
                dto.batteryDesignCapacity(),
                dto.batteryCapacityUnit()
        );
    }
}
