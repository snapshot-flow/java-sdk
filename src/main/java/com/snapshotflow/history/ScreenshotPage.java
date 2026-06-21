package com.snapshotflow.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A page of screenshot history results. */
public final class ScreenshotPage {

    private int total;
    private int limit;
    private int offset;
    private List<ScreenshotSummary> items = new ArrayList<>();

    /** Number of items in this page (not the grand total across all pages). */
    public int total() {
        return total;
    }

    public int limit() {
        return limit;
    }

    public int offset() {
        return offset;
    }

    public List<ScreenshotSummary> items() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public String toString() {
        return "ScreenshotPage{total=" + total + ", limit=" + limit + ", offset=" + offset + '}';
    }
}
