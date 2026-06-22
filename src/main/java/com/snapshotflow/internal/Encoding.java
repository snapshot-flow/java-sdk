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
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is always supported", e);
        }
    }
}
