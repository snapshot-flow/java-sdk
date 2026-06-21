package com.snapshotflow.exception;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Thrown when the API rate limit has been exceeded (HTTP 429,
 * error code {@code RATE_LIMITED}). When the server advertises a
 * {@code Retry-After} (or body {@code retryAfter}) value it is exposed via
 * {@link #retryAfter()} so callers can back off precisely.
 */
public class RateLimitException extends ApiException {

    private static final long serialVersionUID = 1L;

    private final transient Duration retryAfter;

    public RateLimitException(int statusCode, String errorCode, String message, String traceId,
                              Map<String, Object> details, Duration retryAfter) {
        super(statusCode, errorCode, message, traceId, details);
        this.retryAfter = retryAfter;
    }

    /** How long to wait before retrying, if the server told us. */
    public Optional<Duration> retryAfter() {
        return Optional.ofNullable(retryAfter);
    }
}
