package com.snapshotflow.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** URL-encoding helpers. */
public final class Encoding {

    private Encoding() {
    }

    /** Percent-encode a value for use as a single URL path segment. */
    public static String pathSegment(String value) {
        return queryComponent(value).replace("+", "%20");
    }

    /** Percent-encode a query-string key or value ({@code application/x-www-form-urlencoded}, UTF-8). */
    public static String queryComponent(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is always supported", e);
        }
    }
}
