package com.snapshotflow.internal;

import com.snapshotflow.ClientConfig;
import com.snapshotflow.exception.NetworkException;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Default {@link ApiInvoker}: resolves the absolute URL, attaches default
 * headers and the configured {@link Authentication}, sends via the
 * {@link HttpTransport}, retries idempotent requests on transient failures
 * (network errors and {@code 408/429/5xx}), and maps any non-2xx response to a
 * typed exception via {@link ErrorDecoder}.
 */
public final class DefaultApiInvoker implements ApiInvoker {

    private static final Set<Integer> RETRYABLE_STATUSES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(408, 429, 500, 502, 503, 504)));

    private final ClientConfig config;
    private final HttpTransport transport;
    private final Authentication auth;

    public DefaultApiInvoker(ClientConfig config, HttpTransport transport, Authentication auth) {
        this.config = config;
        this.transport = transport;
        this.auth = auth;
    }

    @Override
    public ApiResponse invoke(ApiRequest request) {
        String url = buildUrl(request);
        Map<String, String> headers = buildHeaders(request);
        int maxAttempts = config.maxRetries() + 1;

        for (int attempt = 1; ; attempt++) {
            try {
                ApiResponse response = transport.execute(request.method().name(), url, headers, request.body());
                if (response.isSuccess()) {
                    return response;
                }
                boolean canRetry = request.idempotent()
                        && RETRYABLE_STATUSES.contains(response.status())
                        && attempt < maxAttempts;
                if (!canRetry) {
                    throw ErrorDecoder.decode(response);
                }
                sleep(retryDelay(attempt, response));
            } catch (IOException e) {
                if (!(request.idempotent() && attempt < maxAttempts)) {
                    throw new NetworkException(
                            "Request " + request.method() + " " + url + " failed: " + e.getMessage(), e);
                }
                sleep(config.retryBackoff().multipliedBy(attempt));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new NetworkException(
                        "Request " + request.method() + " " + url + " was interrupted", e);
            }
        }
    }

    private String buildUrl(ApiRequest request) {
        StringBuilder url = new StringBuilder(config.baseUrl()).append(request.path());
        String query = request.query().encode();
        if (!query.isEmpty()) {
            url.append('?').append(query);
        }
        return url.toString();
    }

    private Map<String, String> buildHeaders(ApiRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("User-Agent", config.userAgent());
        headers.put("Accept", "application/json, image/*, application/pdf");
        if (request.contentType() != null) {
            headers.put("Content-Type", request.contentType());
        }
        auth.apply(headers);
        // Per-call headers win over defaults/auth.
        headers.putAll(request.headers());
        return headers;
    }

    private Duration retryDelay(int attempt, ApiResponse response) {
        Optional<String> retryAfter = response.header("retry-after");
        if (retryAfter.isPresent()) {
            try {
                return Duration.ofSeconds(Math.max(0, Long.parseLong(retryAfter.get().trim())));
            } catch (NumberFormatException ignored) {
                // Not a delta-seconds value — fall back to linear backoff.
            }
        }
        return config.retryBackoff().multipliedBy(attempt);
    }

    private static void sleep(Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            return;
        }
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while backing off before retry", e);
        }
    }
}
