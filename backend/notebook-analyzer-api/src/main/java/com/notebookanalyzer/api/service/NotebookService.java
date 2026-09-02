package com.notebookanalyzer.api.service;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.dto.notebook.NotebookRequestDTO;
import com.notebookanalyzer.api.dto.notebook.NotebookResponseDTO;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookAlreadyExistsException;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookHasCollectionsException;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookNotFoundException;
import com.notebookanalyzer.api.mapper.NotebookMapper;
import com.notebookanalyzer.api.repository.NotebookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotebookService {
    private final NotebookRepository notebookRepository;

    public NotebookService(NotebookRepository notebookRepository) {
        this.notebookRepository = notebookRepository;
    }

    private Notebook findByIdOrThrow(Long id) {
        return notebookRepository.findById(id)
                .orElseThrow(() -> new NotebookNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<NotebookResponseDTO> findAll() {
        return notebookRepository.findAll().stream()
                .map(NotebookMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotebookResponseDTO findById(Long id) {
        Notebook target = findByIdOrThrow(id);

        return NotebookMapper.toResponseDTO(target);
    }

    @Transactional(readOnly = true)
    public NotebookResponseDTO findBySerialNumber(String serial) {
        Notebook target = notebookRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new NotebookNotFoundException(serial));

        return NotebookMapper.toResponseDTO(target);
    }

    public NotebookResponseDTO create(NotebookRequestDTO dto) {
        if (dto.serialNumber() != null && notebookRepository.existsBySerialNumber(dto.serialNumber())) {
            throw new NotebookAlreadyExistsException(dto.serialNumber());
        }

        Notebook notebook = NotebookMapper.toEntity(dto);
        Notebook notebookSaved = notebookRepository.save(notebook);
        return NotebookMapper.toResponseDTO(notebookSaved);
    }

    public void deleteById(Long id) {
        Notebook target = findByIdOrThrow(id);

        if (!target.getCollections().isEmpty()) {
            throw new NotebookHasCollectionsException(id);
        }

        notebookRepository.delete(target);
    }
}