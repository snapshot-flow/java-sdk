package com.snapshotflow.internal;

import java.io.IOException;
import java.util.Map;

/**
 * Single-shot HTTP sender. Implementations do exactly one round trip and know
 * nothing about auth, retries or error mapping — those are handled one layer up
 * in {@link DefaultApiInvoker}, which keeps this interface trivially mockable in
 * tests and swappable (e.g. for OkHttp) without touching the rest of the SDK.
 */
public interface HttpTransport {

    ApiResponse execute(String method, String url, Map<String, String> headers, byte[] body)
            throws IOException, InterruptedException;
}
