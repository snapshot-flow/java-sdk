package com.snapshotflow.webhook;

import com.snapshotflow.exception.SnapshotFlowException;

/**
 * Thrown by {@link WebhookVerifier} when an incoming webhook cannot be trusted:
 * missing/malformed signature header, a timestamp outside the freshness window,
 * or an HMAC that does not match. Treat any instance as "reject this request".
 */
public class WebhookVerificationException extends SnapshotFlowException {

    private static final long serialVersionUID = 1L;

    public WebhookVerificationException(String message) {
        super(message);
    }
}
