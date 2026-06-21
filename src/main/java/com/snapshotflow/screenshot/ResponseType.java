package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Shape of the {@code /screenshot} response.
 *
 * <ul>
 *   <li>{@link #IMAGE} — raw binary image/PDF bytes (default).</li>
 *   <li>{@link #JSON} — a JSON object with metadata and (optionally) extracted content, no image bytes.</li>
 *   <li>{@link #BASE64} — a JSON object whose {@code image} field is a {@code data:} URL.</li>
 * </ul>
 */
public enum ResponseType {

    IMAGE("image"),
    JSON("json"),
    BASE64("base64");

    private final String wire;

    ResponseType(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static ResponseType fromWire(String value) {
        for (ResponseType type : values()) {
            if (type.wire.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown response type: " + value);
    }
}
