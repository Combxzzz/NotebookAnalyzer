package com.notebookanalyzer.api.exception.exceptions.notebookcollection;

public class NotebookWithoutCollectionsException extends RuntimeException {
    public NotebookWithoutCollectionsException(Long id) {
        super("Notebook with ID: " + id + " don't have any collection");
    }
}