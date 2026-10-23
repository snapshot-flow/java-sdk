package com.snapshotflow.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.snapshotflow.exception.ApiException;
import com.snapshotflow.exception.AuthenticationException;
import com.snapshotflow.exception.NotFoundException;
import com.snapshotflow.exception.QuotaExceededException;
import com.snapshotflow.exception.RateLimitException;
import com.snapshotflow.exception.SnapshotFlowException;
import com.snapshotflow.exception.ValidationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Translates a non-2xx {@link ApiResponse} into the most specific SDK exception. */
public final class ErrorDecoder {

    private static final Set<String> AUTH_CODES = unmodifiableSet(
            "UNAUTHORIZED", "TOKEN_EXPIRED", "TOKEN_INVALID", "BAD_CREDENTIALS", "ACCOUNT_LOCKED");

    private static final Set<String> VALIDATION_CODES = unmodifiableSet(
            "VALIDATION_ERROR", "INVALID_URL", "INVALID_PARAMS");

    private ErrorDecoder() {
    }

    public static SnapshotFlowException decode(ApiResponse response) {
        int status = response.status();
        String code = null;
        String message = null;
        Map<String, Object> details = new LinkedHashMap<>();

        if (response.body().length > 0 && response.isJson()) {
            try {
                JsonNode root = Json.readTree(response.body());
                if (root.isObject()) {
                    if (root.hasNonNull("error")) {
                        code = root.get("error").asText();
                    }
                    if (root.hasNonNull("message")) {
                        message = root.get("message").asText();
                    }
                    JsonNode detailsNode = root.get("details");
                    if (detailsNode != null && detailsNode.isObject()) {
                        details = convertToMap(detailsNode);
                    } else {
                        // AppError merges extra fields at the top level — collect everything but error/message.
                        Iterator<String> names = root.fieldNames();
                        while (names.hasNext()) {
                            String name = names.next();
                            if (!"error".equals(name) && !"message".equals(name)) {
                                details.put(name, Json.mapper().convertValue(root.get(name), Object.class));
                            }
                        }
                    }
                }
            } catch (RuntimeException ignored) {
                // Unexpected body shape — fall back to status-only classification.
            }
        }

        String traceId = response.header("x-snapshotflow-trace-id").orElse(null);

        if (status == 429 || "RATE_LIMITED".equals(code)) {
            return new RateLimitException(status, code, message, traceId, details, response.retryAfter().orElse(null));
        }
        if (status == 402 || "QUOTA_EXCEEDED".equals(code)) {
            return new QuotaExceededException(status, code, message, traceId, details);
        }
        if (status == 401 || status == 403 || AUTH_CODES.contains(code)) {
            return new AuthenticationException(status, code, message, traceId, details);
        }
        if (status == 404 || "NOT_FOUND".equals(code)) {
            return new NotFoundException(status, code, message, traceId, details);
        }
        if (status == 400 || VALIDATION_CODES.contains(code)) {
            return new ValidationException(status, code, message, traceId, details);
        }
        return new ApiException(status, code, message, traceId, details);
    }

    private static Set<String> unmodifiableSet(String... values) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values)));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> convertToMap(JsonNode node) {
        return Json.mapper().convertValue(node, Map.class);
    }
}
