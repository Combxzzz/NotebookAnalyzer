package com.notebookanalyzer.api.exception;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        Map<String, String> errors
) {
}
