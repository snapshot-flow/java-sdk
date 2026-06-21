package com.snapshotflow.health;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;

/** Service health endpoint ({@code GET /health}). No authentication required. */
public final class HealthApi {

    private final ApiInvoker invoker;

    public HealthApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    public HealthStatus check() {
        ApiResponse response = invoker.invoke(ApiRequest.get("/health").build());
        return Json.read(response.body(), HealthStatus.class);
    }
}
