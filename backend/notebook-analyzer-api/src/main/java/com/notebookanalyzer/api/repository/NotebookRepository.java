package com.notebookanalyzer.api.repository;

import com.notebookanalyzer.api.domain.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long> {

    Optional<Notebook> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
