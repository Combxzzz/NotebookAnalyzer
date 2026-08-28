package com.notebookanalyzer.api.dto.notebook;

import java.time.Instant;

public record NotebookResponseDTO(
        Long id,
        String serialNumber,
        String manufacturer,
        String model,
        Instant createdAt
) {}
