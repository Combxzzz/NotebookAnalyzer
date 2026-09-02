package com.notebookanalyzer.api.service;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.domain.NotebookCollection;
import com.notebookanalyzer.api.dto.NotebookCollectionRegistrationResponseDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionRequestDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionResponseDTO;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookNotFoundException;
import com.notebookanalyzer.api.exception.exceptions.notebookcollection.CollectionNotFoundException;
import com.notebookanalyzer.api.exception.exceptions.notebookcollection.NotebookWithoutCollectionsException;
import com.notebookanalyzer.api.mapper.NotebookCollectionMapper;
import com.notebookanalyzer.api.mapper.NotebookMapper;
import com.notebookanalyzer.api.repository.NotebookCollectionRepository;
import com.notebookanalyzer.api.repository.NotebookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotebookCollectionService {
    private final NotebookRepository notebookRepository;
    private final NotebookCollectionRepository notebookCollectionRepository;

    public NotebookCollectionService(
            NotebookRepository notebookRepository,
            NotebookCollectionRepository notebookCollectionRepository) {

        this.notebookRepository = notebookRepository;
        this.notebookCollectionRepository = notebookCollectionRepository;
    }

    private Notebook findNotebookByIdOrThrow(Long id) {
        return notebookRepository.findById(id)
                .orElseThrow(() -> new NotebookNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<NotebookCollectionResponseDTO> findAllByNotebookIdDesc(Long notebookId) {
        findNotebookByIdOrThrow(notebookId);

        var collections = notebookCollectionRepository.findByNotebookIdOrderByReceivedAtDesc(notebookId);

        return collections.stream()
                .map(NotebookCollectionMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotebookCollectionResponseDTO findLatestCollectionByNotebookId(Long notebookId) {
        findNotebookByIdOrThrow(notebookId);

        NotebookCollection collection = notebookCollectionRepository.findFirstByNotebookIdOrderByReceivedAtDesc(notebookId)
                .orElseThrow(() -> new NotebookWithoutCollectionsException(notebookId));

        return NotebookCollectionMapper.toResponseDTO(collection);
    }

    public NotebookCollectionRegistrationResponseDTO create(NotebookCollectionRequestDTO request) {
        Optional<Notebook> target = notebookRepository.findBySerialNumber(request.notebook().serialNumber());

        Notebook notebook;
        boolean notebookCreated = false;
        if (target.isEmpty()) {
            Notebook newNotebook = NotebookMapper.toEntity(request.notebook());
            notebook = notebookRepository.save(newNotebook);
            notebookCreated = true;
        } else {
            notebook = target.get();
        }

        NotebookCollection collection = NotebookCollectionMapper.toEntity(request, notebook);
        notebook.addCollection(collection);

        NotebookCollection savedCollection = notebookCollectionRepository.save(collection);

        return new NotebookCollectionRegistrationResponseDTO(
                NotebookCollectionMapper.toResponseDTO(savedCollection),
                notebookCreated
        );
    }

    public void deleteByIdWithNotebookId(Long collectionId, Long notebookId) {
        NotebookCollection targetCollection = notebookCollectionRepository.findByIdAndNotebookId(collectionId, notebookId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId, notebookId));

        Notebook targetNotebook = targetCollection.getNotebook();
        targetNotebook.getCollections().remove(targetCollection);

        notebookCollectionRepository.delete(targetCollection);
    }
}
