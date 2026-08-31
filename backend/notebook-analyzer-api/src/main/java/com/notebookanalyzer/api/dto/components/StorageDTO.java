package com.notebookanalyzer.api.dto.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record StorageDTO(
        @NotBlank
        String model,

        @JsonProperty("serial_number")
        @NotBlank
        String serialNumber,

        @Pattern(
                regexp = "^([0-9]+([.,][0-9]+)?\\s*(M|G|T|K|MB|GB|TB|KB)|N/A)$"
        )
        @NotNull
        String size,

        @NotBlank
        String type,

        @JsonProperty("health_status")
        @NotBlank
        String healthStatus,

        @JsonProperty("power_on_hours")
        @NotNull
        @PositiveOrZero
        Long powerOnHours
){}
