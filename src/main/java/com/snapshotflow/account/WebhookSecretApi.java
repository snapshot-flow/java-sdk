package com.snapshotflow.account;

import com.snapshotflow.exception.NotFoundException;
import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;

import java.util.Optional;

/**
 * Manage the user's single webhook signing secret ({@code /api/auth/webhook-secret}).
 * Like {@link ApiKeyApi}, these require a Bearer (JWT) credential.
 */
public final class WebhookSecretApi {

    private final ApiInvoker invoker;

    public WebhookSecretApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Metadata about the configured secret, or empty if none is configured. */
    public Optional<WebhookSecretMetadata> get() {
        try {
            ApiResponse response = invoker.invoke(ApiRequest.get("/api/auth/webhook-secret").build());
            return Optional.of(Json.read(response.body(), WebhookSecretMetadata.class));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    /** Create the signing secret. Fails with a conflict if one already exists — use {@link #rotate()}. */
    public CreatedWebhookSecret create() {
        ApiResponse response = invoker.invoke(ApiRequest.post("/api/auth/webhook-secret").build());
        return Json.read(response.body(), CreatedWebhookSecret.class);
    }

    /** Replace the secret in place; the old secret stops working immediately. */
    public CreatedWebhookSecret rotate() {
        ApiResponse response = invoker.invoke(ApiRequest.post("/api/auth/webhook-secret/rotate").build());
        return Json.read(response.body(), CreatedWebhookSecret.class);
    }

    /** Delete the secret. Subsequent webhook deliveries will be unsigned. */
    public void delete() {
        invoker.invoke(ApiRequest.delete("/api/auth/webhook-secret").build());
    }
}
