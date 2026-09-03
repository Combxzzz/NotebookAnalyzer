package com.notebookanalyzer.api.service;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.domain.NotebookCollection;
import com.notebookanalyzer.api.dto.notebook.NotebookRequestDTO;
import com.notebookanalyzer.api.dto.notebook.NotebookResponseDTO;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookAlreadyExistsException;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookHasCollectionsException;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookNotFoundException;
import com.notebookanalyzer.api.repository.NotebookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotebookServiceTest {

    @Mock
    private NotebookRepository notebookRepository;

    @InjectMocks
    private NotebookService notebookService;

    @Test
    @DisplayName("Should find all notebook")
    void shouldFindAllNotebooks() {
        Notebook notebook1 = Notebook.builder()
                .id(1L)
                .serialNumber("SERIAL-001")
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        Notebook notebook2 = Notebook.builder()
                .id(2L)
                .serialNumber("SERIAL-002")
                .manufacturer("Dell")
                .model("Latitude 5420")
                .build();

        when(notebookRepository.findAll())
                .thenReturn(List.of(notebook1, notebook2));

        List<NotebookResponseDTO> response = notebookService.findAll();

        assertThat(response).hasSize(2);

        assertThat(response.getFirst().id()).isEqualTo(1L);
        assertThat(response.getFirst().serialNumber()).isEqualTo("SERIAL-001");
        assertThat(response.getFirst().manufacturer()).isEqualTo("Lenovo");
        assertThat(response.getFirst().model()).isEqualTo("ThinkPad T480");

        assertThat(response.get(1).id()).isEqualTo(2L);
        assertThat(response.get(1).serialNumber()).isEqualTo("SERIAL-002");
        assertThat(response.get(1).manufacturer()).isEqualTo("Dell");
        assertThat(response.get(1).model()).isEqualTo("Latitude 5420");

        verify(notebookRepository).findAll();
    }

    @Test
    @DisplayName("Should return an empty list")
    void shouldReturnAnEmptyList() {
        when(notebookRepository.findAll())
                .thenReturn(List.of());

        List<NotebookResponseDTO> response = notebookService.findAll();

        assertThat(response).isEmpty();

        verify(notebookRepository).findAll();
    }

    @Test
    @DisplayName("Should find notebook by id")
    void shouldFindById() {
        Notebook notebook1 = Notebook.builder()
                .id(69L)
                .serialNumber("SERIAL-001")
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        when(notebookRepository.findById(69L))
                .thenReturn(Optional.of(notebook1));

        NotebookResponseDTO response = notebookService.findById(69L);

        assertThat(response.id()).isEqualTo(69L);
        assertThat(response.serialNumber()).isEqualTo("SERIAL-001");
        assertThat(response.manufacturer()).isEqualTo("Lenovo");
        assertThat(response.model()).isEqualTo("ThinkPad T480");

        verify(notebookRepository).findById(69L);
    }

    @Test
    @DisplayName("Should throw exception when notebook is not found with the id")
    void shouldThrowExceptionWhenNotebookIsNotFound() {
        when(notebookRepository.findById(69L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notebookService.findById(69L))
                .isInstanceOf(NotebookNotFoundException.class);

        verify(notebookRepository).findById(69L);
    }

    @Test
    @DisplayName("Should find notebook by serial number")
    void shouldFindNotebookBySerialNumber() {
        String serialNumber = "SERIAL-001";

        Notebook notebook1 = Notebook.builder()
                .id(1L)
                .serialNumber(serialNumber)
                .manufacturer("Lenovo")
                .model("ThinkPad T480")
                .build();

        when(notebookRepository.findBySerialNumber(serialNumber))
                .thenReturn(Optional.of(notebook1));

        NotebookResponseDTO response =
                notebookService.findBySerialNumber(serialNumber);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.serialNumber()).isEqualTo("SERIAL-001");
        assertThat(response.manufacturer()).isEqualTo("Lenovo");
        assertThat(response.model()).isEqualTo("ThinkPad T480");

        verify(notebookRepository).findBySerialNumber(serialNumber);
    }

    @Test
    @DisplayName("Should throw exception when notebook is not found with the serial number")
    void shouldThrowExceptionWhenNotebookIsNotFoundWithTheSerialNumber() {
        String serialNumber = "FAKE-SERIAL-001";

        when(notebookRepository.findBySerialNumber(serialNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                notebookService.findBySerialNumber(serialNumber)
        )
                .isInstanceOf(NotebookNotFoundException.class);

        verify(notebookRepository).findBySerialNumber(serialNumber);
    }

    @Test
    @DisplayName("Should create Notebook")
    void shouldCreateNotebook() {
        Long id = 1L;
        String serialNumber = "SERIAL-001";
        String manufacturer = "Lenovo";
        String model = "ThinkPad T480";

        NotebookRequestDTO request = new NotebookRequestDTO(
                serialNumber,
                manufacturer,
                model
        );

        Notebook notebook1 = Notebook.builder()
                .id(id)
                .serialNumber(serialNumber)
                .manufacturer(manufacturer)
                .model(model)
                .build();

        when(notebookRepository.existsBySerialNumber(serialNumber))
                .thenReturn(false);

        when(notebookRepository.save(any(Notebook.class)))
                .thenReturn(notebook1);

        NotebookResponseDTO response = notebookService.create(request);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.serialNumber()).isEqualTo(serialNumber);
        assertThat(response.manufacturer()).isEqualTo(manufacturer);
        assertThat(response.model()).isEqualTo(model);

        verify(notebookRepository).existsBySerialNumber(serialNumber);
        verify(notebookRepository).save(any(Notebook.class));
    }

    @Test
    @DisplayName("Should throw exception when notebook already exists")
    void shouldThrowExceptionWhenNotebookAlreadyExists() {
        String serialNumber = "SERIAL-001";
        String manufacturer = "Lenovo";
        String model = "ThinkPad T480";

        NotebookRequestDTO request = new NotebookRequestDTO(
                serialNumber,
                manufacturer,
                model
        );

        when(notebookRepository.existsBySerialNumber(serialNumber))
                .thenReturn(true);

        assertThatThrownBy(() -> notebookService.create(request))
                .isInstanceOf(NotebookAlreadyExistsException.class);

        verify(notebookRepository).existsBySerialNumber(serialNumber);
        verify(notebookRepository, never()).save(any(Notebook.class));
    }

    @Test
    @DisplayName("Should delete Notebook")
    void shouldDeleteNotebook() {
        Long id = 1L;
        String serialNumber = "SERIAL-001";
        String manufacturer = "Lenovo";
        String model = "ThinkPad T480";

        Notebook notebook1 = Notebook.builder()
                .id(id)
                .serialNumber(serialNumber)
                .manufacturer(manufacturer)
                .model(model)
                .build();

        when(notebookRepository.findById(id))
                .thenReturn(Optional.of(notebook1));

        notebookService.deleteById(id);

        verify(notebookRepository).findById(id);
        verify(notebookRepository).delete(notebook1);
    }

    @Test
    @DisplayName("Should throw exception when notebook has collections")
    void shouldThrowExceptionWhenNotebookHasCollections() {
        Long id = 1L;
        String serialNumber = "SERIAL-001";
        String manufacturer = "Lenovo";
        String model = "ThinkPad T480";

        Notebook notebook1 = Notebook.builder()
                .id(id)
                .serialNumber(serialNumber)
                .manufacturer(manufacturer)
                .model(model)
                .build();

        notebook1.getCollections().add(new NotebookCollection());

        when(notebookRepository.findById(id))
                .thenReturn(Optional.of(notebook1));

        assertThatThrownBy(() -> notebookService.deleteById(id))
                .isInstanceOf(NotebookHasCollectionsException.class);

        verify(notebookRepository).findById(id);
        verify(notebookRepository, never()).delete(any(Notebook.class));
    }

    @Test
    @DisplayName("Should throw exception when notebook does not exist")
    void shouldThrowExceptionWhenNotebookDoesNotExist() {
        Long id = 1L;

        when(notebookRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notebookService.deleteById(id))
                .isInstanceOf(NotebookNotFoundException.class);

        verify(notebookRepository).findById(id);
        verify(notebookRepository, never()).delete(any(Notebook.class));
    }
}