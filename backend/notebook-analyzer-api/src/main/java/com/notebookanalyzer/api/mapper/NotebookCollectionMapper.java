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
        return NotebookCollection.builder()
                .notebook(notebook)
                .cpuModel(dto.cpu().model())
                .cpuArchitecture(dto.cpu().architecture())
                .cpuCores(dto.cpu().cores())
                .cpuThreads(dto.cpu().threads())
                .cpuTemperatureCelsius(dto.cpu().temperatureCelsius())
                .memoryTotalGb(dto.memory().totalGb())
                .memoryType(dto.memory().type())
                .memorySpeedMhz(dto.memory().speedMhz())
                .gpuModel(dto.gpu().model())
                .storageModel(dto.storage().model())
                .storageSerialNumber(dto.storage().serialNumber())
                .storageSize(dto.storage().size())
                .storageType(dto.storage().type())
                .storageHealthStatus(dto.storage().healthStatus())
                .storagePowerOnHours(dto.storage().powerOnHours())
                .batteryHealthPercentage(dto.battery().healthPercentage())
                .batteryCycleCount(dto.battery().cycleCount())
                .batteryFullCapacity(dto.battery().fullCapacity())
                .batteryDesignCapacity(dto.battery().designCapacity())
                .batteryCapacityUnit(dto.battery().capacityUnit())
                .build();
    }
}
