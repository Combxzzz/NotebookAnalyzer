package com.notebookanalyzer.api.mapper;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.dto.notebook.NotebookRequestDTO;
import com.notebookanalyzer.api.dto.notebook.NotebookResponseDTO;

public class NotebookMapper {
    public static NotebookResponseDTO toResponseDTO(Notebook notebook) {
        return new NotebookResponseDTO(
                notebook.getId(),
                notebook.getSerialNumber(),
                notebook.getManufacturer(),
                notebook.getModel(),
                notebook.getCreatedAt()
        );
    }

    public static Notebook toEntity(NotebookRequestDTO dto) {
        return Notebook.builder()
                .serialNumber(dto.serialNumber())
                .manufacturer(dto.manufacturer())
                .model(dto.model())
                .build();
    }
}
