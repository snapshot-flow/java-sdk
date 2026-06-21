package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Paper size used when capturing as {@link OutputFormat#PDF}. */
public enum PdfPaperFormat {

    A4("a4"),
    A3("a3"),
    A2("a2"),
    A1("a1"),
    LETTER("letter"),
    LEGAL("legal"),
    TABLOID("tabloid");

    private final String wire;

    PdfPaperFormat(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static PdfPaperFormat fromWire(String value) {
        for (PdfPaperFormat format : values()) {
            if (format.wire.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown PDF paper format: " + value);
    }
}
