package com.notebookanalyzer.api.dto.notebookcollection;

public record NotebookCollectionRequestDTO(
        Long notebookId,
        String cpuModel,
        String cpuArchitecture,
        Integer cpuCores,
        Integer cpuThreads,
        Integer cpuTemperatureCelsius,
        Integer memoryTotalGb,
        String memoryType,
        Integer memorySpeedMhz,
        String gpuModel,
        String storageModel,
        String storageSerialNumber,
        String storageSize,
        String storageType,
        String storageHealthStatus,
        Long storagePowerOnHours,
        Short batteryHealthPercentage,
        Integer batteryCycleCount,
        Long batteryFullCapacity,
        Long batteryDesignCapacity,
        String batteryCapacityUnit

        // TODO: VERIFICAR COM A IA DEPOIS (TODOS OS OUTROS DTOS TBM)
) {}
