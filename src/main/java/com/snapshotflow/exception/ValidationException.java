package com.snapshotflow.exception;

import java.util.Map;

/**
 * Thrown when the request was rejected as invalid before any capture happened
 * (HTTP 400). Covers error codes such as {@code VALIDATION_ERROR},
 * {@code INVALID_URL} and {@code INVALID_PARAMS}.
 *
 * <p>For {@code VALIDATION_ERROR} the server attaches per-field messages under
 * {@link #details()} (shape: {@code { "fieldName": ["message", ...] }}).
 */
public class ValidationException extends ApiException {

    private static final long serialVersionUID = 1L;

    public ValidationException(int statusCode, String errorCode, String message, String traceId,
                               Map<String, Object> details) {
        super(statusCode, errorCode, message, traceId, details);
    }
}
