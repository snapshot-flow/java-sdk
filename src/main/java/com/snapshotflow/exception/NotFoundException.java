package com.snapshotflow.exception;

import java.util.Map;

/**
 * Thrown when the requested resource does not exist (HTTP 404) — for example a
 * job id, screenshot id or API key id that was never issued, already deleted,
 * or belongs to another user.
 */
public class NotFoundException extends ApiException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(int statusCode, String errorCode, String message, String traceId,
                             Map<String, Object> details) {
        super(statusCode, errorCode, message, traceId, details);
    }
}
