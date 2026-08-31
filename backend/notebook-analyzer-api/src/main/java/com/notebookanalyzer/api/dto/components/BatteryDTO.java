package com.notebookanalyzer.api.dto.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record BatteryDTO(

        @JsonProperty("health_percentage")
        @Min(0)
        @Max(100)
        Short healthPercentage,

        @JsonProperty("cycle_count")
        @PositiveOrZero
        Integer cycleCount,

        @JsonProperty("full_capacity")
        @PositiveOrZero
        Long fullCapacity,

        @JsonProperty("design_capacity")
        @PositiveOrZero
        Long designCapacity,

        @JsonProperty("capacity_unit")
        String capacityUnit
) {}
