package com.snapshotflow.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Lifecycle state of an async capture job. */
public enum JobStatus {

    PENDING("pending"),
    PROCESSING("processing"),
    DONE("done"),
    FAILED("failed");

    private final String wire;

    JobStatus(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static JobStatus fromWire(String value) {
        for (JobStatus status : values()) {
            if (status.wire.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown job status: " + value);
    }

    /** Whether the job has reached a final state ({@link #DONE} or {@link #FAILED}). */
    public boolean isTerminal() {
        return this == DONE || this == FAILED;
    }
}
