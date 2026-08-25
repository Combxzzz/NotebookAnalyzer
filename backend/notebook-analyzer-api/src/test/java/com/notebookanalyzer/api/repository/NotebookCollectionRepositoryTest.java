package com.notebookanalyzer.api.repository;

import com.notebookanalyzer.api.domain.Notebook;
import com.notebookanalyzer.api.domain.NotebookCollection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

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
    @DisplayName("Must save the collection and fetch by notebookId ordered by receipt date descending")
    void shouldSaveAndFindByNotebookId() {
        Notebook notebook = new Notebook();
        notebook.setManufacturer("Dell");
        notebook.setModel("Latitude 5420");
        notebook.setSerialNumber("TEST-DELL-999");
        Notebook savedNotebook = notebookRepository.save(notebook);

        NotebookCollection collection1 = new NotebookCollection();
        collection1.setCpuModel("Intel Core i5-1135G7");
        collection1.setMemoryTotalGb(16);
        collection1.setStorageType("SSD");
        notebook.addCollection(collection1);

        notebookCollectionRepository.save(collection1);

        List<NotebookCollection> collections = notebookCollectionRepository.findByNotebookIdOrderByReceivedAtDesc(savedNotebook.getId());

        assertThat(collections).hasSize(1);
        assertThat(collections.get(0).getCpuModel()).isEqualTo("Intel Core i5-1135G7");
        assertThat(collections.get(0).getMemoryTotalGb()).isEqualTo(16);
        assertThat(collections.get(0).getNotebook().getId()).isEqualTo(savedNotebook.getId());

        Optional<NotebookCollection> latest = notebookCollectionRepository.findFirstByNotebookIdOrderByReceivedAtDesc(savedNotebook.getId());
        assertThat(latest).isPresent();
        assertThat(latest.get().getId()).isEqualTo(collection1.getId());
    }
}
