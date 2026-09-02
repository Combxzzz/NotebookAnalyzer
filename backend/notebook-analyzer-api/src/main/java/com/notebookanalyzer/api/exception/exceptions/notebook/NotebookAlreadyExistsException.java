package com.notebookanalyzer.api.exception.exceptions.notebook;

public class NotebookAlreadyExistsException extends RuntimeException {
    public NotebookAlreadyExistsException(String serialNumber) {
        super("Notebook with serial number: " + serialNumber + " already exists");
    }
}
