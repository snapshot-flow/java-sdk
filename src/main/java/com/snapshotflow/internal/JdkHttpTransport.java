package com.snapshotflow.internal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Default {@link HttpTransport} built on the JDK's {@link HttpClient}
 * (Java 11+). This is why the SDK needs no third-party HTTP dependency.
 */
public final class JdkHttpTransport implements HttpTransport {

    private final HttpClient client;
    private final Duration requestTimeout;

    public JdkHttpTransport(Duration connectTimeout, Duration requestTimeout) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.requestTimeout = requestTimeout;
    }

    @Override
    public ApiResponse execute(String method, String url, Map<String, String> headers, byte[] body)
            throws IOException, InterruptedException {

        HttpRequest.BodyPublisher publisher = (body == null || body.length == 0)
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(requestTimeout)
                .method(method, publisher);

        for (Map.Entry<String, String> h : headers.entrySet()) {
            builder.header(h.getKey(), h.getValue());
        }

        HttpResponse<byte[]> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());

        Map<String, String> responseHeaders = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : response.headers().map().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                responseHeaders.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue().get(0));
            }
        }

        return new ApiResponse(response.statusCode(), responseHeaders, response.body());
    }
}
