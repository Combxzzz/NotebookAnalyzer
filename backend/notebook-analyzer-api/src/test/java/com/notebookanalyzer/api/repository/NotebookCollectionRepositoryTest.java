package com.notebookanalyzer.api.repository;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.domain.NotebookCollection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotebookCollectionRepositoryTest {

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private NotebookCollectionRepository notebookCollectionRepository;

    @Test
    @DisplayName("Must return collections ordered by receivedAt DESC and fetch the latest one")
    void shouldReturnCollectionsOrderedByReceivedAtDesc() throws InterruptedException {

        Notebook notebookBuild = Notebook.builder()
                .manufacturer("Dell")
                .model("Latitude 5420")
                .serialNumber("TEST-DELL-999")
                .createdAt(Instant.parse("2026-08-29T10:00:00Z"))
                .build();

        Notebook notebook = notebookRepository.saveAndFlush(notebookBuild);

        NotebookCollection firstCollection = NotebookCollection.builder()
                .cpuModel("Intel Core i5-1135G7")
                .memoryTotalGb(8)
                .receivedAt(Instant.parse("2026-08-30T10:00:00Z"))
                .build();
        notebook.addCollection(firstCollection);
        notebookCollectionRepository.saveAndFlush(firstCollection);

        NotebookCollection secondCollection = NotebookCollection.builder()
                .cpuModel("Intel Core i5-1135G7")
                .memoryTotalGb(16)
                .receivedAt(Instant.parse("2026-08-30T11:00:00Z"))
                .build();
        notebook.addCollection(secondCollection);
        notebookCollectionRepository.saveAndFlush(secondCollection);

        List<NotebookCollection> collections = notebookCollectionRepository
                .findByNotebookIdOrderByReceivedAtDesc(notebook.getId());

        assertThat(collections).hasSize(2);
        assertThat(collections.getFirst().getId()).isEqualTo(secondCollection.getId());
        assertThat(collections.get(1).getId()).isEqualTo(firstCollection.getId());

        Optional<NotebookCollection> latest = notebookCollectionRepository
                .findFirstByNotebookIdOrderByReceivedAtDesc(notebook.getId());

        assertThat(latest).isPresent();
        assertThat(latest.get().getId()).isEqualTo(secondCollection.getId());
        assertThat(latest.get().getMemoryTotalGb()).isEqualTo(16);
    }
}
