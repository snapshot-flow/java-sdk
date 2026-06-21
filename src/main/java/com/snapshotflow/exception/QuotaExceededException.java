package com.snapshotflow.exception;

import java.util.Map;

/**
 * Thrown when the account's screenshot quota is exhausted (error code
 * {@code QUOTA_EXCEEDED}). Inspect the {@code X-Quota-*} response headers, or
 * call {@code client.auth().me()}, to see the current usage and limit.
 */
public class QuotaExceededException extends ApiException {

    private static final long serialVersionUID = 1L;

    public QuotaExceededException(int statusCode, String errorCode, String message, String traceId,
                                  Map<String, Object> details) {
        super(statusCode, errorCode, message, traceId, details);
    }
}
