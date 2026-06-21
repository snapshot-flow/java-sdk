package com.snapshotflow.internal;

/** SDK-wide constants. */
public final class Sdk {

    private Sdk() {
    }

    /** Keep in sync with the {@code <version>} in pom.xml. */
    public static final String VERSION = "1.0.0-SNAPSHOT";

    public static final String USER_AGENT =
            "snapshotflow-java/" + VERSION + " (Java " + System.getProperty("java.version") + ")";
}
