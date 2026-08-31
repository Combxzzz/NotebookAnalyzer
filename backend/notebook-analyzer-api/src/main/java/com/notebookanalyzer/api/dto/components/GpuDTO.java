package com.notebookanalyzer.api.dto.components;

import jakarta.validation.constraints.NotBlank;

public record GpuDTO(
        @NotBlank
        String model
) {
}
