package com.snapshotflow.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** URL-encoding helpers. */
public final class Encoding {

    private Encoding() {
    }

    /** Percent-encode a value for use as a single URL path segment. */
    public static String pathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
