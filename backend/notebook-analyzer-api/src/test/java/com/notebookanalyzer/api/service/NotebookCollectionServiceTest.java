package com.notebookanalyzer.api.service;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.domain.NotebookCollection;
import com.notebookanalyzer.api.dto.NotebookCollectionRegistrationResponseDTO;
import com.notebookanalyzer.api.dto.components.*;
import com.notebookanalyzer.api.dto.notebook.NotebookRequestDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionRequestDTO;
import com.notebookanalyzer.api.dto.notebookcollection.NotebookCollectionResponseDTO;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookNotFoundException;
import com.notebookanalyzer.api.exception.exceptions.notebookcollection.NotebookWithoutCollectionsException;
import com.notebookanalyzer.api.mapper.NotebookCollectionMapper;
import com.notebookanalyzer.api.repository.NotebookCollectionRepository;
import com.notebookanalyzer.api.repository.NotebookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotebookCollectionServiceTest {
    @Mock
    private NotebookRepository notebookRepository;

    @Mock
    private NotebookCollectionRepository notebookCollectionRepository;

    @InjectMocks
    private NotebookCollectionService notebookCollectionService;

    @Test
    @DisplayName("Should return all collections ordered by receivedAt DESC")
    void shouldReturnAllCollectionsOrderedByReceivedAtDesc() {
        Long notebookId = 1L;

        Notebook notebook = Notebook.builder()
                .id(notebookId)
                .serialNumber("SERIAL-001")
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        NotebookCollection collection1 = NotebookCollection.builder()
                .id(1L)
                .notebook(notebook)
                .receivedAt(Instant.parse("2026-08-30T10:00:00Z"))
                .build();

        NotebookCollection collection2 = NotebookCollection.builder()
                .id(2L)
                .notebook(notebook)
                .receivedAt(Instant.parse("2026-08-30T11:00:00Z"))
                .build();

        when(notebookRepository.findById(notebookId))
                .thenReturn(Optional.of(notebook));

        when(notebookCollectionRepository
                .findByNotebookIdOrderByReceivedAtDesc(notebookId))
                .thenReturn(List.of(collection2, collection1));

        List<NotebookCollectionResponseDTO> response =
                notebookCollectionService.findAllByNotebookIdDesc(notebookId);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).id()).isEqualTo(2L);
        assertThat(response.get(1).id()).isEqualTo(1L);

        verify(notebookRepository).findById(notebookId);

        verify(notebookCollectionRepository)
                .findByNotebookIdOrderByReceivedAtDesc(notebookId);
    }

    @Test
    @DisplayName("Should throw exception when notebook does not exist")
    void shouldThrowExceptionWhenNotebookDoesNotExist() {
        Long notebookId = 1L;

        when(notebookRepository.findById(notebookId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotebookNotFoundException.class,
                () -> notebookCollectionService.findAllByNotebookIdDesc(notebookId)
        );

        verify(notebookRepository).findById(notebookId);

        verify(
                notebookCollectionRepository,
                never()
        ).findByNotebookIdOrderByReceivedAtDesc(notebookId);
    }

    @Test
    @DisplayName("Should return an empty list when notebook has no collections")
    void shouldReturnEmptyListWhenNotebookHasNoCollections() {
        Long notebookId = 1L;

        Notebook notebook = Notebook.builder()
                .id(notebookId)
                .serialNumber("SERIAL-001")
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        when(notebookRepository.findById(notebookId))
                .thenReturn(Optional.of(notebook));

        when(notebookCollectionRepository
                .findByNotebookIdOrderByReceivedAtDesc(notebookId))
                .thenReturn(List.of());

        List<NotebookCollectionResponseDTO> response =
                notebookCollectionService.findAllByNotebookIdDesc(notebookId);

        assertTrue(response.isEmpty());

        verify(notebookRepository).findById(notebookId);

        verify(notebookCollectionRepository)
                .findByNotebookIdOrderByReceivedAtDesc(notebookId);
    }

    @Test
    @DisplayName("Should return the latest collection of the notebook")
    void shouldReturnLatestCollectionOfNotebook() {
        Long notebookId = 1L;

        Notebook notebook = Notebook.builder()
                .id(notebookId)
                .serialNumber("SERIAL-001")
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        NotebookCollection collection = NotebookCollection.builder()
                .id(10L)
                .notebook(notebook)
                .cpuModel("Intel Core i5-1135G7")
                .memoryTotalGb(16)
                .receivedAt(Instant.parse("2026-08-30T11:00:00Z"))
                .build();

        when(notebookRepository.findById(notebookId))
                .thenReturn(Optional.of(notebook));

        when(notebookCollectionRepository
                .findFirstByNotebookIdOrderByReceivedAtDesc(notebookId))
                .thenReturn(Optional.of(collection));

        NotebookCollectionResponseDTO response =
                notebookCollectionService.findLatestCollectionByNotebookId(notebookId);

        assertThat(response.id()).isEqualTo(collection.getId());
        assertThat(response.cpuModel()).isEqualTo(collection.getCpuModel());
        assertThat(response.memoryTotalGb()).isEqualTo(collection.getMemoryTotalGb());

        verify(notebookRepository).findById(notebookId);

        verify(notebookCollectionRepository)
                .findFirstByNotebookIdOrderByReceivedAtDesc(notebookId);
    }

    @Test
    @DisplayName("Should throw exception when notebook has no collections")
    void shouldThrowExceptionWhenNotebookHasNoCollections() {
        Long notebookId = 1L;

        Notebook notebook = Notebook.builder()
                .id(notebookId)
                .serialNumber("SERIAL-001")
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        when(notebookRepository.findById(notebookId))
                .thenReturn(Optional.of(notebook));

        when(notebookCollectionRepository
                .findFirstByNotebookIdOrderByReceivedAtDesc(notebookId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                notebookCollectionService.findLatestCollectionByNotebookId(notebookId)
        )
                .isInstanceOf(NotebookWithoutCollectionsException.class);

        verify(notebookRepository).findById(notebookId);

        verify(notebookCollectionRepository)
                .findFirstByNotebookIdOrderByReceivedAtDesc(notebookId);
    }

    @Test
    @DisplayName("Should throw exception when notebook does not exist")
    void shouldThrowExceptionWhenNotebookDoesNotExist2() {
        Long notebookId = 1L;

        when(notebookRepository.findById(notebookId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                notebookCollectionService.findLatestCollectionByNotebookId(notebookId)
        )
                .isInstanceOf(NotebookNotFoundException.class);

        verify(notebookRepository).findById(notebookId);

        verify(notebookCollectionRepository, never())
                .findFirstByNotebookIdOrderByReceivedAtDesc(notebookId);
    }

    @Test
    @DisplayName("Should create collection for an existing notebook")
    void shouldCreateCollectionForExistingNotebook() {
        String serialNumber = "SERIAL-001";

        NotebookCollectionRequestDTO request = new NotebookCollectionRequestDTO(
                new NotebookRequestDTO(
                        serialNumber,
                        "Lenovo",
                        "ThinkPad T480"
                ),
                new CpuDTO(
                        "Intel Core i5-1135G7",
                        "64x",
                        16,
                        18,
                        39
                ),
                new MemoryDTO(
                        16,
                        "DDR4",
                        3200
                ),
                new GpuDTO(
                        "Vega 7"
                ),
                new StorageDTO(
                        "Samsung",
                        "STORAGE-SERIAL-001",
                        "500GB",
                        "SSD",
                        "PASSED",
                        1600L
                ),
                new BatteryDTO(
                        Short.valueOf("67"),
                        66,
                        16000L,
                        20000L,
                        "mHZ"
                )
        );

        Notebook notebook = Notebook.builder()
                .id(1L)
                .serialNumber(serialNumber)
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        NotebookCollection collection = NotebookCollectionMapper.toEntity(request, notebook);
        collection.setId(10L);

        when(notebookRepository.findBySerialNumber(serialNumber))
                .thenReturn(Optional.of(notebook));

        when(notebookCollectionRepository.save(any(NotebookCollection.class)))
                .thenReturn(collection);

        NotebookCollectionRegistrationResponseDTO response =
                notebookCollectionService.create(request);

        assertThat(response.notebookCreated()).isFalse();

        assertThat(response.notebookCollection().id())
                .isEqualTo(collection.getId());

        assertThat(response.notebookCollection().cpuModel())
                .isEqualTo(collection.getCpuModel());

        verify(notebookRepository)
                .findBySerialNumber(serialNumber);

        verify(notebookRepository, never())
                .save(any(Notebook.class));

        verify(notebookCollectionRepository)
                .save(any(NotebookCollection.class));
    }
}
