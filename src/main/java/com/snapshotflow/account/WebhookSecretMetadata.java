package com.snapshotflow.account;

import java.util.Optional;

/** Non-secret metadata about the user's webhook signing secret ({@code GET /api/auth/webhook-secret}). */
public final class WebhookSecretMetadata {

    private String secretPrefix;
    private String createdAt;
    private String rotatedAt;

    /** Non-secret prefix of the signing secret, for display. */
    public String secretPrefix() {
        return secretPrefix;
    }

    public String createdAt() {
        return createdAt;
    }

    public Optional<String> rotatedAt() {
        return Optional.ofNullable(rotatedAt);
    }

    @Override
    public String toString() {
        return "WebhookSecretMetadata{secretPrefix=" + secretPrefix + ", rotatedAt=" + rotatedAt + '}';
    }
}
