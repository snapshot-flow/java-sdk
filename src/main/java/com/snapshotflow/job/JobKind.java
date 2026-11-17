package com.snapshotflow.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Whether an async job is a single-page capture or a batch capture. */
public enum JobKind {

    SINGLE("single"),
    BATCH("batch");

    private final String wire;

    JobKind(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static JobKind fromWire(String value) {
        for (JobKind kind : values()) {
            if (kind.wire.equalsIgnoreCase(value)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Unknown job kind: " + value);
    }
}
