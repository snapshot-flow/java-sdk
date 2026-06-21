package com.snapshotflow.batch;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.Validate;

/** Batch capture of multiple URLs in one request ({@code POST /batch}). */
public final class BatchApi {

    private final ApiInvoker invoker;

    public BatchApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    public BatchResult capture(BatchRequest request) {
        Validate.notNull(request, "request");
        ApiResponse response = invoker.invoke(ApiRequest.post("/batch").jsonBody(request.toBody()).build());
        return Json.read(response.body(), BatchResult.class);
    }

    /** Capture several URLs with default options. */
    public BatchResult capture(String... urls) {
        return capture(BatchRequest.urls(urls).build());
    }
}
