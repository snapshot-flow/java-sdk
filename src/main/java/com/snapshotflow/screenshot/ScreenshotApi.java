package com.snapshotflow.screenshot;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.QueryParams;
import com.snapshotflow.internal.Validate;
import com.snapshotflow.job.JobSubmission;

/** The main capture endpoint ({@code GET /screenshot}), both synchronous and async. */
public final class ScreenshotApi {

    private final ApiInvoker invoker;

    public ScreenshotApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Capture {@code url} synchronously with all defaults. */
    public ScreenshotResult capture(String url) {
        return capture(ScreenshotRequest.url(url).build());
    }

    /**
     * Capture synchronously and return the rendered bytes. When the request asks for
     * {@code metadata} or {@code extractContent} the SDK transparently uses the base64
     * response so the bytes <em>and</em> the metadata/content come back in one call.
     */
    public ScreenshotResult capture(ScreenshotRequest request) {
        Validate.notNull(request, "request");
        QueryParams query = request.toQuery();
        query.add("response_type", request.wantsJsonResponse() ? "base64" : "image");
        query.add("async", "false");
        ApiResponse response = invoker.invoke(ApiRequest.get("/screenshot").query(query).build());
        return ScreenshotResult.fromResponse(response, request.formatOrDefault());
    }

    /**
     * Submit an async capture job and return immediately. Poll the result with
     * {@code client.jobs().await(submission.jobId(), timeout)}, or supply a
     * {@code webhookUrl} on the request to be notified on completion.
     */
    public JobSubmission submit(ScreenshotRequest request) {
        Validate.notNull(request, "request");
        QueryParams query = request.toQuery();
        query.add("async", "true");
        // Enqueue is not safely retryable — a retry after a transient failure would
        // create a duplicate job, so this request opts out of automatic retries.
        ApiResponse response = invoker.invoke(
                ApiRequest.get("/screenshot").query(query).idempotent(false).build());
        return Json.read(response.body(), JobSubmission.class);
    }
}
