package com.notebookanalyzer.api.dto.notebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record NotebookRequestDTO(
        @JsonProperty("serial_number")
        @NotBlank
        String serialNumber,

        @NotBlank
        String manufacturer,

        @NotBlank
        String model
) {}
