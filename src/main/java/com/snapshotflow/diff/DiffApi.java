package com.snapshotflow.diff;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Validate;
import com.snapshotflow.screenshot.ResponseType;

/** Pixel-level visual diff between two pages ({@code GET /diff}). */
public final class DiffApi {

    private final ApiInvoker invoker;

    public DiffApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Diff two URLs with default options (returns stats and the diff image). */
    public DiffResult compare(String before, String after) {
        return compare(DiffRequest.of(before, after).build());
    }

    public DiffResult compare(DiffRequest request) {
        Validate.notNull(request, "request");
        // Default to base64 so callers get both the stats and the rendered diff image.
        ResponseType effective = request.responseType() != null ? request.responseType() : ResponseType.BASE64;
        ApiResponse response = invoker.invoke(
                ApiRequest.get("/diff").query(request.toQuery(effective)).build());
        return DiffResult.fromResponse(response);
    }
}
