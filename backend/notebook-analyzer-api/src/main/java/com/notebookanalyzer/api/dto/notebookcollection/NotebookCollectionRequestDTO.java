package com.notebookanalyzer.api.dto.notebookcollection;

import com.notebookanalyzer.api.dto.components.*;
import com.notebookanalyzer.api.dto.notebook.NotebookRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record NotebookCollectionRequestDTO(
        @NotNull @Valid NotebookRequestDTO notebook,
        @NotNull @Valid CpuDTO cpu,
        @NotNull @Valid MemoryDTO memory,
        @NotNull @Valid GpuDTO gpu,
        @NotNull @Valid StorageDTO storage,
        @NotNull @Valid BatteryDTO battery
) {}
