package com.notebookanalyzer.api.exception.exceptions.notebook;

public class NotebookHasCollectionsException extends RuntimeException {
    public NotebookHasCollectionsException(Long id) {
        super("Notebook with ID: " + id + " still have collections");
    }
}
