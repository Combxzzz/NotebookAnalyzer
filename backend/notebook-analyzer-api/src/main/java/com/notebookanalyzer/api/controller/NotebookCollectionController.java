package com.notebookanalyzer.api.controller;

import com.notebookanalyzer.api.dto.NotebookCollectionRegistrationResponseDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionRequestDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionResponseDTO;
import com.notebookanalyzer.api.service.NotebookCollectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NotebookCollectionController {
    private final NotebookCollectionService collectionService;

    public NotebookCollectionController(NotebookCollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/notebooks/{notebookId}/collections")
    public List<NotebookCollectionResponseDTO> findAllByNotebook(@PathVariable Long notebookId) {
        return collectionService.findAllByNotebookIdDesc(notebookId);
    }

    @GetMapping("/notebooks/{notebookId}/collections/latest")
    public NotebookCollectionResponseDTO findLatest(@PathVariable Long notebookId) {
        return collectionService.findLatestCollectionByNotebookId(notebookId);
    }

    @PostMapping("/collections")
    public ResponseEntity<NotebookCollectionRegistrationResponseDTO> create(@Valid @RequestBody NotebookCollectionRequestDTO request) {
        NotebookCollectionRegistrationResponseDTO response = collectionService.create(request);
        Long collectionId = response.notebookCollection().id();
        Long notebookId = response.notebookCollection().notebookId();
        URI location = URI.create(String.format("/api/notebooks/%d/collections/%d", notebookId, collectionId));
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/notebooks/{notebookId}/collections/{collectionId}")
    public ResponseEntity<Void> delete(@PathVariable Long notebookId, @PathVariable Long collectionId) {
        collectionService.deleteByIdWithNotebookId(collectionId, notebookId);
        return ResponseEntity.noContent().build();
    }
}
