package com.snapshotflow;

import java.time.Duration;

/**
 * Immutable transport configuration for a {@link SnapshotFlowClient}. Build one
 * through {@link SnapshotFlowClient#builder()} rather than constructing it
 * directly.
 */
public final class ClientConfig {

    private final String baseUrl;
    private final Duration connectTimeout;
    private final Duration requestTimeout;
    private final int maxRetries;
    private final Duration retryBackoff;
    private final String userAgent;

    public ClientConfig(String baseUrl, Duration connectTimeout, Duration requestTimeout,
                        int maxRetries, Duration retryBackoff, String userAgent) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
        this.maxRetries = maxRetries;
        this.retryBackoff = retryBackoff;
        this.userAgent = userAgent;
    }

    /** API base URL, with any trailing slash removed (e.g. {@code https://api.snapshotflow.com}). */
    public String baseUrl() {
        return baseUrl;
    }

    public Duration connectTimeout() {
        return connectTimeout;
    }

    public Duration requestTimeout() {
        return requestTimeout;
    }

    /** Number of additional attempts for idempotent requests after the first failure. */
    public int maxRetries() {
        return maxRetries;
    }

    public Duration retryBackoff() {
        return retryBackoff;
    }

    public String userAgent() {
        return userAgent;
    }
}
