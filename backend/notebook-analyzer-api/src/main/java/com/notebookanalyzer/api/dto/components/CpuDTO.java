package com.notebookanalyzer.api.dto.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CpuDTO(
        @NotNull
        String model,

        @NotBlank
        String architecture,

        @NotNull
        @Positive
        Integer cores,

        @NotNull
        @Positive
        Integer threads,

        @JsonProperty("temperature_celsius")
        @NotNull
        Integer temperatureCelsius
) {
}
