package com.snapshotflow.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Ordered, insertion-preserving builder for a URL query string. {@code null}
 * values are skipped, so request builders can pass every optional parameter and
 * only the ones actually set end up on the wire — letting the server apply its
 * own defaults for the rest.
 */
public final class QueryParams {

    private final List<String[]> params = new ArrayList<>();

    public QueryParams add(String key, String value) {
        if (value != null) {
            params.add(new String[]{key, value});
        }
        return this;
    }

    public QueryParams add(String key, Integer value) {
        return value == null ? this : add(key, value.toString());
    }

    public QueryParams add(String key, Long value) {
        return value == null ? this : add(key, value.toString());
    }

    public QueryParams add(String key, Double value) {
        return value == null ? this : add(key, stripTrailingZero(value));
    }

    public QueryParams add(String key, Boolean value) {
        return value == null ? this : add(key, value ? "true" : "false");
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    /** Render as {@code key=value&key=value}, percent-encoded. Empty string when no params. */
    public String encode() {
        StringBuilder sb = new StringBuilder();
        for (String[] p : params) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(enc(p[0])).append('=').append(enc(p[1]));
        }
        return sb.toString();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /** Render doubles without a trailing ".0" so e.g. 1.0 → "1" matches integer-ish server params. */
    private static String stripTrailingZero(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }
}
