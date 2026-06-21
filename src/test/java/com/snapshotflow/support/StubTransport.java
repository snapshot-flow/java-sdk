package com.snapshotflow.support;

import com.snapshotflow.internal.ApiResponse;
import com.snapshotflow.internal.HttpTransport;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Test double for {@link HttpTransport}: records every request and returns
 * either queued canned responses or a per-request handler's output. Plug it in
 * with {@code SnapshotFlowClient.builder().transport(stub)} to exercise the full
 * invoker (auth headers, URL building, retries, error decoding) with no network.
 */
public final class StubTransport implements HttpTransport {

    /** A captured outgoing request. */
    public static final class Recorded {
        public final String method;
        public final String url;
        public final Map<String, String> headers;
        public final byte[] body;

        Recorded(String method, String url, Map<String, String> headers, byte[] body) {
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
        }

        public String bodyAsString() {
            return body == null ? "" : new String(body, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    public final List<Recorded> requests = new ArrayList<>();
    private final Deque<Object> responses = new ArrayDeque<>();
    private Function<Recorded, ApiResponse> handler;

    /** Queue a canned response (FIFO). */
    public StubTransport enqueue(ApiResponse response) {
        responses.add(response);
        return this;
    }

    /** Queue an IOException to simulate a transport failure. */
    public StubTransport enqueueIoError() {
        responses.add(new IOException("simulated connection failure"));
        return this;
    }

    /** Compute each response from the request instead of queuing. */
    public StubTransport handler(Function<Recorded, ApiResponse> handler) {
        this.handler = handler;
        return this;
    }

    public Recorded last() {
        return requests.get(requests.size() - 1);
    }

    public int callCount() {
        return requests.size();
    }

    @Override
    public ApiResponse execute(String method, String url, Map<String, String> headers, byte[] body)
            throws IOException {
        requests.add(new Recorded(method, url, new LinkedHashMap<>(headers), body));
        if (handler != null) {
            return handler.apply(last());
        }
        Object next = responses.poll();
        if (next == null) {
            throw new IllegalStateException("StubTransport has no queued response for " + method + " " + url);
        }
        if (next instanceof IOException) {
            throw (IOException) next;
        }
        return (ApiResponse) next;
    }
}
