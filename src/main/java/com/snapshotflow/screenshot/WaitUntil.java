package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** When navigation is considered finished before the screenshot is taken (Puppeteer {@code waitUntil}). */
public enum WaitUntil {

    /** Wait for the {@code load} event. */
    LOAD("load"),
    /** Wait for {@code DOMContentLoaded}. Good for SPAs that fetch data after mount. */
    DOM_CONTENT_LOADED("domcontentloaded"),
    /** Wait until there are no network connections for at least 500ms. */
    NETWORK_IDLE_0("networkidle0"),
    /** Wait until there are no more than 2 network connections for at least 500ms (default). */
    NETWORK_IDLE_2("networkidle2");

    private final String wire;

    WaitUntil(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String wire() {
        return wire;
    }

    @JsonCreator
    public static WaitUntil fromWire(String value) {
        for (WaitUntil waitUntil : values()) {
            if (waitUntil.wire.equalsIgnoreCase(value)) {
                return waitUntil;
            }
        }
        throw new IllegalArgumentException("Unknown waitUntil: " + value);
    }
}
