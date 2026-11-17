package com.snapshotflow.batch;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.Validate;
import com.snapshotflow.job.JobSubmission;

/** Batch capture of multiple URLs in one request ({@code POST /batch}), synchronous or async. */
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

    /**
     * Submit an async batch capture job ({@code POST /batch} with {@code async: true}) and return
     * immediately. Poll progress and results with {@code client.jobs().await(submission.jobId(), timeout)}.
     */
    public JobSubmission submit(BatchRequest request) {
        Validate.notNull(request, "request");
        ApiResponse response = invoker.invoke(
                ApiRequest.post("/batch").jsonBody(request.toAsyncBody()).idempotent(false).build());
        return Json.read(response.body(), JobSubmission.class);
    }

    /** Submit an async batch capture job for several URLs with default options. */
    public JobSubmission submit(String... urls) {
        return submit(BatchRequest.urls(urls).build());
    }
}
