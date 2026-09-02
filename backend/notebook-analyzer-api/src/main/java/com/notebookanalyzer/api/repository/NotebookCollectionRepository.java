package com.notebookanalyzer.api.repository;

import com.notebookanalyzer.api.domain.NotebookCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotebookCollectionRepository extends JpaRepository<NotebookCollection, Long> {

    List<NotebookCollection> findByNotebookIdOrderByReceivedAtDesc(Long notebookId);

    Optional<NotebookCollection> findFirstByNotebookIdOrderByReceivedAtDesc(Long notebookId);

    Optional<NotebookCollection> findByIdAndNotebookId(Long id, Long notebookId);
}
