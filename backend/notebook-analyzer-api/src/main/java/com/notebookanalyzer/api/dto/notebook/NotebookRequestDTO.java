package com.notebookanalyzer.api.dto.notebook;

public record NotebookRequestDTO(
        String serialNumber,
        String manufacturer,
        String model
) {}
