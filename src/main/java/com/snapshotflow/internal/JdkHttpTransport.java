package com.snapshotflow.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Default {@link HttpTransport}, built on the JDK's {@link HttpURLConnection}
 * (available since Java 1.1). Using the legacy built-in client — rather than the
 * Java 11 {@code java.net.http.HttpClient} — is what lets the SDK run on Java 8
 * while still pulling in no third-party HTTP dependency.
 *
 * <p>The SDK only issues {@code GET}, {@code POST} and {@code DELETE} requests,
 * all of which {@code HttpURLConnection} supports natively, so none of the usual
 * {@code PATCH} workarounds are required.
 */
public final class JdkHttpTransport implements HttpTransport {

    private final int connectTimeoutMillis;
    private final int requestTimeoutMillis;

    public JdkHttpTransport(Duration connectTimeout, Duration requestTimeout) {
        this.connectTimeoutMillis = toMillis(connectTimeout);
        this.requestTimeoutMillis = toMillis(requestTimeout);
    }

    @Override
    public ApiResponse execute(String method, String url, Map<String, String> headers, byte[] body)
            throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            conn.setRequestMethod(method);
            conn.setConnectTimeout(connectTimeoutMillis);
            // HttpURLConnection has no total-request deadline; the read timeout
            // (max idle between bytes) is the closest equivalent to the request
            // timeout the HttpClient transport applied per call.
            conn.setReadTimeout(requestTimeoutMillis);
            conn.setInstanceFollowRedirects(true);

            for (Map.Entry<String, String> h : headers.entrySet()) {
                conn.setRequestProperty(h.getKey(), h.getValue());
            }

            // Only enable output for requests that actually carry a body: calling
            // setDoOutput(true) on a GET would silently switch it to POST.
            if (body != null && body.length > 0) {
                conn.setDoOutput(true);
                try (OutputStream out = conn.getOutputStream()) {
                    out.write(body);
                }
            }

            int status = conn.getResponseCode();
            byte[] responseBody = readBody(conn, status);
            return new ApiResponse(status, collectHeaders(conn), responseBody);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 2xx/3xx bodies come from {@code getInputStream}; 4xx/5xx bodies from
     * {@code getErrorStream}, which is {@code null} for empty error responses.
     */
    private static byte[] readBody(HttpURLConnection conn, int status) throws IOException {
        InputStream stream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null) {
            return new byte[0];
        }
        try (InputStream in = stream) {
            return readAll(in);
        }
    }

    // Java 8 has no InputStream.readAllBytes(); read the stream by hand.
    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int read;
        while ((read = in.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    private static Map<String, String> collectHeaders(HttpURLConnection conn) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            String name = entry.getKey();
            // The HTTP status line is exposed under a null key — skip it.
            if (name == null || entry.getValue().isEmpty()) {
                continue;
            }
            result.put(name.toLowerCase(Locale.ROOT), entry.getValue().get(0));
        }
        return result;
    }

    private static int toMillis(Duration duration) {
        long millis = duration.toMillis();
        return millis > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) millis;
    }
}
