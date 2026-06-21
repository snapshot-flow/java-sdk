package com.snapshotflow.exception;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Thrown when the SnapshotFlow API returns a non-2xx response. Mirrors the
 * server's error envelope: {@code { "error": CODE, "message": "...", ... }}.
 *
 * <p>More specific subtypes are thrown when the status / error code is
 * recognised ({@link AuthenticationException}, {@link ValidationException},
 * {@link RateLimitException}, {@link QuotaExceededException},
 * {@link NotFoundException}); a plain {@code ApiException} is used otherwise.
 */
public class ApiException extends SnapshotFlowException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String errorCode;
    private final transient Map<String, Object> details;
    private final String traceId;

    public ApiException(int statusCode, String errorCode, String message, String traceId,
                        Map<String, Object> details) {
        super(buildMessage(statusCode, errorCode, message));
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.traceId = traceId;
        this.details = details == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(details);
    }

    private static String buildMessage(int statusCode, String errorCode, String message) {
        StringBuilder sb = new StringBuilder("SnapshotFlow API error ").append(statusCode);
        if (errorCode != null && !errorCode.isEmpty()) {
            sb.append(" [").append(errorCode).append(']');
        }
        if (message != null && !message.isEmpty()) {
            sb.append(": ").append(message);
        }
        return sb.toString();
    }

    /** HTTP status code of the response (e.g. 400, 401, 429, 500). */
    public int statusCode() {
        return statusCode;
    }

    /** Machine-readable error code from the response body's {@code error} field, or {@code null}. */
    public String errorCode() {
        return errorCode;
    }

    /** Any extra fields the server attached to the error (e.g. validation field errors). Never {@code null}. */
    public Map<String, Object> details() {
        return details;
    }

    /** The {@code X-SnapshotFlow-Trace-Id} response header, when present — quote it in support tickets. */
    public Optional<String> traceId() {
        return Optional.ofNullable(traceId);
    }
}
