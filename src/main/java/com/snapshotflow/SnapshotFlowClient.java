package com.snapshotflow;

import com.snapshotflow.account.ApiKeyApi;
import com.snapshotflow.account.AuthApi;
import com.snapshotflow.account.WebhookSecretApi;
import com.snapshotflow.batch.BatchApi;
import com.snapshotflow.diff.DiffApi;
import com.snapshotflow.health.HealthApi;
import com.snapshotflow.history.HistoryApi;
import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.Authentication;
import com.snapshotflow.internal.DefaultApiInvoker;
import com.snapshotflow.internal.HttpTransport;
import com.snapshotflow.internal.JdkHttpTransport;
import com.snapshotflow.internal.Sdk;
import com.snapshotflow.job.JobApi;
import com.snapshotflow.screenshot.ScreenshotApi;
import com.snapshotflow.screenshot.ScreenshotRequest;
import com.snapshotflow.screenshot.ScreenshotResult;

import java.time.Duration;

/**
 * Entry point to the SnapshotFlow API. Immutable and thread-safe — build one and
 * share it across your application.
 *
 * <pre>{@code
 * SnapshotFlowClient client = SnapshotFlowClient.builder()
 *         .apiKey("e2e_your_key")
 *         .build();
 *
 * ScreenshotResult shot = client.capture("https://example.com");
 * shot.writeTo(Path.of("example.png"));
 * }</pre>
 *
 * Operations are grouped into sub-APIs: {@link #screenshots()}, {@link #batch()},
 * {@link #diff()}, {@link #jobs()}, {@link #history()}, {@link #auth()},
 * {@link #apiKeys()}, {@link #webhookSecrets()} and {@link #health()}. Account
 * management ({@code apiKeys}, {@code webhookSecrets}, {@code auth().me()})
 * requires a Bearer (JWT) credential — obtain one with {@code auth().login(...)}
 * and derive a client via {@link #withBearerToken(String)}.
 */
public final class SnapshotFlowClient {

    /** Default production base URL. */
    public static final String DEFAULT_BASE_URL = "https://api.snapshotflow.com";

    private final ClientConfig config;
    private final HttpTransport transport;

    private final ScreenshotApi screenshots;
    private final BatchApi batch;
    private final DiffApi diff;
    private final JobApi jobs;
    private final HistoryApi history;
    private final AuthApi authApi;
    private final ApiKeyApi apiKeys;
    private final WebhookSecretApi webhookSecrets;
    private final HealthApi health;

    private SnapshotFlowClient(ClientConfig config, HttpTransport transport, Authentication authentication) {
        this.config = config;
        this.transport = transport;
        ApiInvoker invoker = new DefaultApiInvoker(config, transport, authentication);
        this.screenshots = new ScreenshotApi(invoker);
        this.batch = new BatchApi(invoker);
        this.diff = new DiffApi(invoker);
        this.jobs = new JobApi(invoker);
        this.history = new HistoryApi(invoker);
        this.authApi = new AuthApi(invoker);
        this.apiKeys = new ApiKeyApi(invoker);
        this.webhookSecrets = new WebhookSecretApi(invoker);
        this.health = new HealthApi(invoker);
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Shortcut for {@code builder().apiKey(apiKey).build()}. */
    public static SnapshotFlowClient create(String apiKey) {
        return builder().apiKey(apiKey).build();
    }

    // ── Sub-APIs ────────────────────────────────────────────────────────────

    public ScreenshotApi screenshots() {
        return screenshots;
    }

    public BatchApi batch() {
        return batch;
    }

    public DiffApi diff() {
        return diff;
    }

    public JobApi jobs() {
        return jobs;
    }

    public HistoryApi history() {
        return history;
    }

    public AuthApi auth() {
        return authApi;
    }

    public ApiKeyApi apiKeys() {
        return apiKeys;
    }

    public WebhookSecretApi webhookSecrets() {
        return webhookSecrets;
    }

    public HealthApi health() {
        return health;
    }

    public ClientConfig config() {
        return config;
    }

    // ── Top-level shortcuts ─────────────────────────────────────────────────

    /** Shortcut for {@code screenshots().capture(url)}. */
    public ScreenshotResult capture(String url) {
        return screenshots.capture(url);
    }

    /** Shortcut for {@code screenshots().capture(request)}. */
    public ScreenshotResult capture(ScreenshotRequest request) {
        return screenshots.capture(request);
    }

    // ── Credential switching (cheap — reuses the same transport) ─────────────

    /** A view of this client authenticated with the given API key. */
    public SnapshotFlowClient withApiKey(String apiKey) {
        return new SnapshotFlowClient(config, transport, Authentication.apiKey(apiKey));
    }

    /** A view of this client authenticated with the given Bearer (JWT) token. */
    public SnapshotFlowClient withBearerToken(String token) {
        return new SnapshotFlowClient(config, transport, Authentication.bearer(token));
    }

    /** A view of this client with no credentials. */
    public SnapshotFlowClient withoutAuth() {
        return new SnapshotFlowClient(config, transport, Authentication.NONE);
    }

    /** Fluent builder for {@link SnapshotFlowClient}. */
    public static final class Builder {
        private String baseUrl = DEFAULT_BASE_URL;
        private String apiKey;
        private String bearerToken;
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(60);
        private int maxRetries = 2;
        private Duration retryBackoff = Duration.ofMillis(500);
        private String userAgent = Sdk.USER_AGENT;
        private HttpTransport transport;

        /** API base URL. Defaults to {@link #DEFAULT_BASE_URL}; set to your own host for self-hosting. */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /** Authenticate with a user-scoped API key ({@code e2e_...}). */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /** Authenticate with a Bearer (JWT) token. Takes precedence over an API key if both are set. */
        public Builder bearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /** Extra attempts for idempotent requests on transient failures (default 2; 0 disables retries). */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = Math.max(0, maxRetries);
            return this;
        }

        public Builder retryBackoff(Duration retryBackoff) {
            this.retryBackoff = retryBackoff;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /** Advanced/testing: supply a custom transport instead of the default JDK transport. */
        public Builder transport(HttpTransport transport) {
            this.transport = transport;
            return this;
        }

        public SnapshotFlowClient build() {
            ClientConfig config = new ClientConfig(stripTrailingSlashes(baseUrl), connectTimeout,
                    requestTimeout, maxRetries, retryBackoff, userAgent);
            HttpTransport effectiveTransport = transport != null
                    ? transport
                    : new JdkHttpTransport(connectTimeout, requestTimeout);
            Authentication authentication;
            if (bearerToken != null) {
                authentication = Authentication.bearer(bearerToken);
            } else if (apiKey != null) {
                authentication = Authentication.apiKey(apiKey);
            } else {
                authentication = Authentication.NONE;
            }
            return new SnapshotFlowClient(config, effectiveTransport, authentication);
        }

        private static String stripTrailingSlashes(String url) {
            if (url == null) {
                throw new IllegalArgumentException("baseUrl must not be null");
            }
            int end = url.length();
            while (end > 0 && url.charAt(end - 1) == '/') {
                end--;
            }
            return url.substring(0, end);
        }
    }
}
