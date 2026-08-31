package com.notebookanalyzer.api.dto.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MemoryDTO(
        @JsonProperty("total_gb")
        @NotNull
        @Positive
        Integer totalGb,

        @NotBlank
        String type,

        @JsonProperty("speed_mhz")
        @Positive
        Integer speedMhz
) {
}
