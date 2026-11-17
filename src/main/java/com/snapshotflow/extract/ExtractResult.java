package com.snapshotflow.extract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snapshotflow.screenshot.ContentFormat;

/** Extracted page content returned by {@code GET /extract}. */
public final class ExtractResult {

    private String url;
    private ContentFormat format;
    private String content;
    private int chars;
    private boolean truncated;
    @JsonProperty("took_ms")
    private long tookMs;

    public String url() {
        return url;
    }

    public ContentFormat format() {
        return format;
    }

    public String content() {
        return content;
    }

    public int chars() {
        return chars;
    }

    public boolean truncated() {
        return truncated;
    }

    public long tookMs() {
        return tookMs;
    }

    @Override
    public String toString() {
        return "ExtractResult{url=" + url + ", format=" + format + ", chars=" + chars
                + ", truncated=" + truncated + ", tookMs=" + tookMs + '}';
    }
}
