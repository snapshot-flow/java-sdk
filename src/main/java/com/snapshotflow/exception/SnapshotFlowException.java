package com.snapshotflow.exception;

/**
 * Base type for every error raised by the SnapshotFlow SDK.
 *
 * <p>Unchecked so calling code is not forced to wrap every request in a
 * {@code try/catch}; catch this type when you want to handle any SDK failure
 * uniformly, or one of its subtypes for finer control:
 *
 * <ul>
 *   <li>{@link ApiException} — the API returned a non-2xx response
 *       (and its subtypes {@link AuthenticationException},
 *       {@link ValidationException}, {@link RateLimitException},
 *       {@link QuotaExceededException}, {@link NotFoundException}).</li>
 *   <li>{@link NetworkException} — the request never produced a response
 *       (connection refused, timeout, TLS error, thread interrupted).</li>
 * </ul>
 */
public class SnapshotFlowException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SnapshotFlowException(String message) {
        super(message);
    }

    public SnapshotFlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
