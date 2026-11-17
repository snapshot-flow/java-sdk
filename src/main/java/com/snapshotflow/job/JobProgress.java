package com.snapshotflow.job;

/** Progress counters of a batch capture {@link Job}. */
public final class JobProgress {

    private int completed;
    private int total;

    public int completed() {
        return completed;
    }

    public int total() {
        return total;
    }

    @Override
    public String toString() {
        return "JobProgress{completed=" + completed + ", total=" + total + '}';
    }
}
