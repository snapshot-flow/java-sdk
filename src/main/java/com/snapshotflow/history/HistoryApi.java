package com.snapshotflow.history;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Encoding;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.QueryParams;
import com.snapshotflow.internal.Validate;

/**
 * Screenshot history for authenticated users ({@code /screenshots}). Requires a
 * credential and that the server has history enabled; otherwise the API returns
 * a {@code 503}.
 */
public final class HistoryApi {

    private final ApiInvoker invoker;

    public HistoryApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** First page of history (limit 50, offset 0), newest first. */
    public ScreenshotPage list() {
        return list(50, 0);
    }

    public ScreenshotPage list(int limit, int offset) {
        QueryParams query = new QueryParams().add("limit", limit).add("offset", offset);
        ApiResponse response = invoker.invoke(ApiRequest.get("/screenshots").query(query).build());
        return Json.read(response.body(), ScreenshotPage.class);
    }

    /** Fetch one screenshot with a freshly signed download URL (default TTL). */
    public ScreenshotDetail get(String screenshotId) {
        return get(screenshotId, null);
    }

    /** Fetch one screenshot, requesting a download URL valid for {@code ttlSeconds} (60–604800). */
    public ScreenshotDetail get(String screenshotId, Integer ttlSeconds) {
        Validate.notBlank(screenshotId, "screenshotId");
        QueryParams query = new QueryParams().add("ttl", ttlSeconds);
        ApiResponse response = invoker.invoke(
                ApiRequest.get("/screenshots/" + Encoding.pathSegment(screenshotId)).query(query).build());
        return Json.read(response.body(), ScreenshotDetail.class);
    }

    /** Permanently delete a stored screenshot. */
    public void delete(String screenshotId) {
        Validate.notBlank(screenshotId, "screenshotId");
        invoker.invoke(ApiRequest.delete("/screenshots/" + Encoding.pathSegment(screenshotId)).build());
    }
}
