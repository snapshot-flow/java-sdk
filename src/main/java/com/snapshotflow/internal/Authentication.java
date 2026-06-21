package com.snapshotflow.internal;

import java.util.Map;

/**
 * Strategy for attaching credentials to an outgoing request. The SDK supports
 * the two schemes the API accepts: a user-scoped API key ({@code X-Api-Key})
 * and a Bearer JWT ({@code Authorization: Bearer}).
 */
@FunctionalInterface
public interface Authentication {

    void apply(Map<String, String> headers);

    /** No credentials — used when {@code AUTH_ENABLED=false} on the server, or for the auth endpoints. */
    Authentication NONE = headers -> {
    };

    static Authentication apiKey(String key) {
        Validate.notBlank(key, "apiKey");
        return headers -> headers.put("X-Api-Key", key);
    }

    static Authentication bearer(String token) {
        Validate.notBlank(token, "bearerToken");
        return headers -> headers.put("Authorization", "Bearer " + token);
    }
}
