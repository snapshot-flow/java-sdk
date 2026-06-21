package com.snapshotflow.internal;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Immutable view of a single HTTP response: status, headers (lower-cased keys) and raw body bytes. */
public final class ApiResponse {

    private final int status;
    private final Map<String, String> headers;
    private final byte[] body;

    public ApiResponse(int status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers == null ? Collections.emptyMap() : headers;
        this.body = body == null ? new byte[0] : body;
    }

    public int status() {
        return status;
    }

    public byte[] body() {
        return body;
    }

    public String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public Optional<String> header(String name) {
        return Optional.ofNullable(headers.get(name.toLowerCase(Locale.ROOT)));
    }

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }

    public boolean isJson() {
        return header("content-type").map(c -> c.toLowerCase(Locale.ROOT).contains("json")).orElse(false);
    }
}
