package com.snapshotflow.account;

/** A user's screenshot quota usage. */
public final class Quota {

    private long used;
    private long limit;
    private long remaining;

    public long used() {
        return used;
    }

    public long limit() {
        return limit;
    }

    public long remaining() {
        return remaining;
    }

    @Override
    public String toString() {
        return "Quota{used=" + used + ", limit=" + limit + ", remaining=" + remaining + '}';
    }
}
