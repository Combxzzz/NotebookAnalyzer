package com.notebookanalyzer.api.repository;

import com.notebookanalyzer.api.domain.Notebook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotebookRepositoryTest {

    @Autowired
    private NotebookRepository notebookRepository;

    @Test
    @DisplayName("Must save and find the notebook by serial number")
    void shouldSaveAndFindBySerialNumber() {
        Notebook notebook = Notebook.builder()
                        .manufacturer("Lenovo")
                        .model("ThinkPad T14")
                        .serialNumber("TEST-SERIAL-123")
                        .createdAt(Instant.parse("2025-08-30T10:00:00Z"))
                        .build();

        notebookRepository.save(notebook);

        Optional<Notebook> found = notebookRepository.findBySerialNumber("TEST-SERIAL-123");

        assertThat(found).isPresent();
        assertThat(found.get().getManufacturer()).isEqualTo("Lenovo");
        assertThat(found.get().getModel()).isEqualTo("ThinkPad T14");
        assertThat(found.get().getId()).isNotNull();
        assertThat(notebookRepository.existsBySerialNumber("TEST-SERIAL-123")).isTrue();
    }

    @Test
    @DisplayName("Must return empty when serial number is not found")
    void shouldReturnEmptyWhenSerialNumberNotFound() {
        Optional<Notebook> found = notebookRepository.findBySerialNumber("NON-EXISTENT-SERIAL");

        assertThat(found).isEmpty();
        assertThat(notebookRepository.existsBySerialNumber("NON-EXISTENT-SERIAL")).isFalse();
    }
}
