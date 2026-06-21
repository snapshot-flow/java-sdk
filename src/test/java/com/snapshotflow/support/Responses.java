package com.snapshotflow.support;

import com.snapshotflow.internal.ApiResponse;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Convenience builders for canned {@link ApiResponse}s in tests. */
public final class Responses {

    private Responses() {
    }

    public static ApiResponse json(int status, String body) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("content-type", "application/json; charset=utf-8");
        return new ApiResponse(status, headers, body.getBytes(StandardCharsets.UTF_8));
    }

    public static ApiResponse binary(int status, String contentType, byte[] body, Map<String, String> extraHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("content-type", contentType);
        if (extraHeaders != null) {
            extraHeaders.forEach((k, v) -> headers.put(k.toLowerCase(java.util.Locale.ROOT), v));
        }
        return new ApiResponse(status, headers, body);
    }

    public static ApiResponse empty(int status) {
        return new ApiResponse(status, new LinkedHashMap<>(), new byte[0]);
    }

    public static Map<String, String> headers(String... kv) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            headers.put(kv[i], kv[i + 1]);
        }
        return headers;
    }
}
