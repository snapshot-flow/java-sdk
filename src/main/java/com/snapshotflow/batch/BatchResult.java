package com.snapshotflow.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Result of a {@code POST /batch} call: one {@link BatchItemResult} per submitted URL. */
public final class BatchResult {

    private int total;
    private List<BatchItemResult> results = new ArrayList<>();

    public int total() {
        return total;
    }

    public List<BatchItemResult> results() {
        return Collections.unmodifiableList(results);
    }

    /** Items that captured successfully. */
    public List<BatchItemResult> successes() {
        return results.stream().filter(r -> !r.hasError()).collect(Collectors.toList());
    }

    /** Items that failed (e.g. quota exhausted partway through). */
    public List<BatchItemResult> failures() {
        return results.stream().filter(BatchItemResult::hasError).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "BatchResult{total=" + total + ", failures=" + failures().size() + '}';
    }
}
