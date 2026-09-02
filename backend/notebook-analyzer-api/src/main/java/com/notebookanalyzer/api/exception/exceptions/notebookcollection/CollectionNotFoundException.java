package com.notebookanalyzer.api.exception.exceptions.notebookcollection;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(Long collectionId, Long notebookId) {
        super("Collection with ID: " + collectionId + " not found in notebook with ID: " + notebookId);
    }
}
