package com.snapshotflow.account;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Encoding;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Manage user-scoped API keys ({@code /api/auth/api-keys}). These endpoints
 * require a <strong>Bearer (JWT)</strong> credential — by design an API key
 * cannot mint or rotate sibling keys.
 */
public final class ApiKeyApi {

    private final ApiInvoker invoker;

    public ApiKeyApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Create a new API key with no label. */
    public CreatedApiKey create() {
        return create(null);
    }

    /** Create a new API key with an optional human label. */
    public CreatedApiKey create(String label) {
        Map<String, Object> body = label == null
                ? Collections.emptyMap()
                : Collections.singletonMap("label", label);
        ApiResponse response = invoker.invoke(ApiRequest.post("/api/auth/api-keys").jsonBody(body).build());
        return Json.read(response.body(), CreatedApiKey.class);
    }

    /** List the user's API keys (active and revoked). */
    public List<ApiKeySummary> list() {
        ApiResponse response = invoker.invoke(ApiRequest.get("/api/auth/api-keys").build());
        ListResponse parsed = Json.read(response.body(), ListResponse.class);
        return parsed.items != null ? parsed.items : new ArrayList<>();
    }

    /** Revoke a key immediately. */
    public void revoke(String apiKeyId) {
        Validate.notBlank(apiKeyId, "apiKeyId");
        invoker.invoke(ApiRequest.delete("/api/auth/api-keys/" + Encoding.pathSegment(apiKeyId)).build());
    }

    /** Atomically revoke a key and issue a replacement with the same label. */
    public CreatedApiKey rotate(String apiKeyId) {
        Validate.notBlank(apiKeyId, "apiKeyId");
        ApiResponse response = invoker.invoke(
                ApiRequest.post("/api/auth/api-keys/" + Encoding.pathSegment(apiKeyId) + "/rotate").build());
        return Json.read(response.body(), CreatedApiKey.class);
    }

    static final class ListResponse {
        int total;
        List<ApiKeySummary> items;
    }
}
