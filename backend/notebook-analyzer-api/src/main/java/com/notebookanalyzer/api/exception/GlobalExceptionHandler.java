package com.notebookanalyzer.api.exception;

import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookAlreadyExistsException;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookHasCollectionsException;
import com.notebookanalyzer.api.exception.exceptions.notebook.NotebookNotFoundException;
import com.notebookanalyzer.api.exception.exceptions.notebookcollection.CollectionNotFoundException;
import com.notebookanalyzer.api.exception.exceptions.notebookcollection.NotebookWithoutCollectionsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus httpStatus,
            Exception exception
    ) {
        ErrorResponse response = new ErrorResponse(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );

        return ResponseEntity
                .status(httpStatus)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNullElse(
                                fieldError.getDefaultMessage(),
                                "Invalid value"
                        ),
                        (existingMessage, newMessage) -> existingMessage + "; " + newMessage
                ));

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                Instant.now(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    // NOTEBOOK EXCEPTIONS

    @ExceptionHandler(NotebookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotebookNotFound(
            NotebookNotFoundException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(NotebookHasCollectionsException.class)
    public ResponseEntity<ErrorResponse> handleNotebookHasCollections(
            NotebookHasCollectionsException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(NotebookAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleNotebookAlreadyExistsException(
            NotebookAlreadyExistsException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception);
    }


    // COLLECTION EXCEPTIONS

    @ExceptionHandler(NotebookWithoutCollectionsException.class)
    public ResponseEntity<ErrorResponse> handleNotebookWithoutCollectionsException(
            NotebookWithoutCollectionsException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(CollectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCollectionNotFoundException(
            CollectionNotFoundException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception);
    }
}
