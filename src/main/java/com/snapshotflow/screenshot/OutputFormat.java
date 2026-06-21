package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Output format of a capture. {@link #PDF} produces a document; the others produce images. */
public enum OutputFormat {

    PNG("png"),
    JPEG("jpeg"),
    WEBP("webp"),
    PDF("pdf");

    private final String wire;

    OutputFormat(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static OutputFormat fromWire(String value) {
        for (OutputFormat format : values()) {
            if (format.wire.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown output format: " + value);
    }

    public boolean isPdf() {
        return this == PDF;
    }

    /** The HTTP content type the API returns for this format. */
    public String mimeType() {
        return this == PDF ? "application/pdf" : "image/" + wire;
    }
}
