package com.snapshotflow.exception;

import java.util.Map;

/**
 * Thrown for authentication / authorization failures: missing, invalid,
 * revoked or expired credentials (HTTP 401), or a credential that lacks the
 * required privilege (HTTP 403 — e.g. trying to manage API keys with an API
 * key instead of a JWT).
 *
 * <p>Recognised error codes include {@code UNAUTHORIZED}, {@code TOKEN_EXPIRED},
 * {@code TOKEN_INVALID}, {@code BAD_CREDENTIALS}, {@code ACCOUNT_LOCKED}.
 */
public class AuthenticationException extends ApiException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(int statusCode, String errorCode, String message, String traceId,
                                   Map<String, Object> details) {
        super(statusCode, errorCode, message, traceId, details);
    }
}
