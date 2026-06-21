package com.snapshotflow.account;

import java.util.Optional;

/**
 * A newly created or rotated API key. The raw {@link #key()} is returned
 * <strong>once</strong> — store it now; the server keeps only a hash.
 */
public final class CreatedApiKey {

    private String apiKeyId;
    private String key;
    private String keyPrefix;
    private String label;
    private String createdAt;
    private String previousApiKeyId;

    public String apiKeyId() {
        return apiKeyId;
    }

    /** The full secret key (format {@code e2e_...}). Shown only on creation/rotation. */
    public String key() {
        return key;
    }

    public String keyPrefix() {
        return keyPrefix;
    }

    public Optional<String> label() {
        return Optional.ofNullable(label);
    }

    public String createdAt() {
        return createdAt;
    }

    /** On rotation, the id of the key this one replaced. */
    public Optional<String> previousApiKeyId() {
        return Optional.ofNullable(previousApiKeyId);
    }

    @Override
    public String toString() {
        return "CreatedApiKey{apiKeyId=" + apiKeyId + ", keyPrefix=" + keyPrefix + ", key=***}";
    }
}
