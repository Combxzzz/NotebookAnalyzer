package com.notebookanalyzer.api.dto;

import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionResponseDTO;

public record NotebookCollectionRegistrationResponseDTO(
        NotebookCollectionResponseDTO notebookCollection,
        boolean notebookCreated
) {
}
