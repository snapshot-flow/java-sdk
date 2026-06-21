package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Format of extracted page content when {@code extractContent} is enabled. */
public enum ContentFormat {

    HTML("html"),
    MARKDOWN("markdown"),
    TEXT("text");

    private final String wire;

    ContentFormat(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static ContentFormat fromWire(String value) {
        for (ContentFormat format : values()) {
            if (format.wire.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown content format: " + value);
    }
}
