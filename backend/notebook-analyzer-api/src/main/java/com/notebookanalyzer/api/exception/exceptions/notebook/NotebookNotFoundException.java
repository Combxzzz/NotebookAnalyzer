package com.notebookanalyzer.api.exception.exceptions.notebook;

public class NotebookNotFoundException extends RuntimeException {
    public NotebookNotFoundException(Long id) {
        super("Notebook not found with ID: " + id);
    }

    public NotebookNotFoundException(String serial) {
        super("Notebook not found with Serial: " + serial);
    }
}
