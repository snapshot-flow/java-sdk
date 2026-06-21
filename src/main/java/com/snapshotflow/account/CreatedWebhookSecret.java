package com.snapshotflow.account;

import java.util.Optional;

/**
 * A newly created or rotated webhook signing secret. The raw {@link #secret()}
 * is returned <strong>once</strong> — store it now (encrypted at rest on the
 * server, unrecoverable if lost). Use it with
 * {@code com.snapshotflow.webhook.WebhookVerifier} on your receiver.
 */
public final class CreatedWebhookSecret {

    private String secret;
    private String secretPrefix;
    private String createdAt;
    private String rotatedAt;

    /** The full signing secret (format {@code whsec_...}). Shown only on creation/rotation. */
    public String secret() {
        return secret;
    }

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
        return "CreatedWebhookSecret{secretPrefix=" + secretPrefix + ", secret=***}";
    }
}
