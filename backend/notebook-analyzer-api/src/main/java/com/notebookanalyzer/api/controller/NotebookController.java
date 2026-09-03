package com.notebookanalyzer.api.controller;

import com.notebookanalyzer.api.dto.notebook.NotebookRequestDTO;
import com.notebookanalyzer.api.dto.notebook.NotebookResponseDTO;
import com.notebookanalyzer.api.service.NotebookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notebooks")
public class NotebookController {
    private final NotebookService notebookService;

    public NotebookController(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    @GetMapping
    public List<NotebookResponseDTO> findAll() {
        return notebookService.findAll();
    }

    @GetMapping("/{id}")
    public NotebookResponseDTO findById(@PathVariable Long id) {
        return notebookService.findById(id);
    }

    @GetMapping("/serial/{serial}")
    public NotebookResponseDTO findBySerial(@PathVariable("serial") String serial) {
        return notebookService.findBySerialNumber(serial);
    }

    @PostMapping
    public ResponseEntity<NotebookResponseDTO> create(@Valid @RequestBody NotebookRequestDTO dto) {
        NotebookResponseDTO created = notebookService.create(dto);
        URI location = URI.create(String.format("/api/notebooks/%d", created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notebookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
