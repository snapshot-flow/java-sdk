package com.snapshotflow.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** How each batch item is returned. */
public enum BatchResponseType {

    /** Inline base64 {@code data:} URLs (default). */
    BASE64("base64"),
    /** Storage paths on disk (local storage mode). */
    PATHS("paths"),
    /** Public image URLs. */
    IMAGE_URLS("image_urls");

    private final String wire;

    BatchResponseType(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static BatchResponseType fromWire(String value) {
        for (BatchResponseType type : values()) {
            if (type.wire.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown batch response type: " + value);
    }
}
