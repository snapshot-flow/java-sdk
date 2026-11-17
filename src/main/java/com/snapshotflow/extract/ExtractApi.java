package com.snapshotflow.extract;

import com.snapshotflow.internal.ApiInvoker;
import com.snapshotflow.internal.ApiRequest;
import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.Validate;

/** Clean content extraction from live web pages ({@code GET /extract}). */
public final class ExtractApi {

    private final ApiInvoker invoker;

    public ExtractApi(ApiInvoker invoker) {
        this.invoker = invoker;
    }

    /** Extract clean Markdown content from {@code url} using server defaults. */
    public ExtractResult extract(String url) {
        return extract(ExtractRequest.url(url).build());
    }

    /** Extract page content with the given options. */
    public ExtractResult extract(ExtractRequest request) {
        Validate.notNull(request, "request");
        ApiResponse response = invoker.invoke(ApiRequest.get("/extract").query(request.toQuery()).build());
        return Json.read(response.body(), ExtractResult.class);
    }
}
