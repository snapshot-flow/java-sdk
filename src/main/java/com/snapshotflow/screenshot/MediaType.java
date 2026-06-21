package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** CSS media type to emulate while rendering. */
public enum MediaType {

    SCREEN("screen"),
    PRINT("print");

    private final String wire;

    MediaType(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static MediaType fromWire(String value) {
        for (MediaType type : values()) {
            if (type.wire.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown media type: " + value);
    }
}
