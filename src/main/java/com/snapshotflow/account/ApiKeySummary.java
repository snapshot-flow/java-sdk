package com.snapshotflow.account;

import java.util.Optional;

/** A user-scoped API key as it appears in {@code GET /api/auth/api-keys} (no raw secret). */
public final class ApiKeySummary {

    private String apiKeyId;
    private String keyPrefix;
    private String label;
    private String createdAt;
    private String lastUsedAt;
    private boolean revoked;

    public String apiKeyId() {
        return apiKeyId;
    }

    /** Non-secret prefix of the key, for display. */
    public String keyPrefix() {
        return keyPrefix;
    }

    public Optional<String> label() {
        return Optional.ofNullable(label);
    }

    public String createdAt() {
        return createdAt;
    }

    public Optional<String> lastUsedAt() {
        return Optional.ofNullable(lastUsedAt);
    }

    public boolean revoked() {
        return revoked;
    }

    @Override
    public String toString() {
        return "ApiKeySummary{apiKeyId=" + apiKeyId + ", keyPrefix=" + keyPrefix + ", revoked=" + revoked + '}';
    }
}
