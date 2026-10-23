package com.snapshotflow.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** SDK-wide constants. */
public final class Sdk {

    private static final String VERSION_RESOURCE = "/com/snapshotflow/version.properties";
    private static final String UNKNOWN_VERSION = "unknown";

    private Sdk() {
    }

    /** The SDK version, stamped into {@code version.properties} by Maven resource filtering at build time. */
    public static final String VERSION = loadVersion();

    public static final String USER_AGENT =
            "snapshotflow-java/" + VERSION + " (Java " + System.getProperty("java.version") + ")";

    private static String loadVersion() {
        try (InputStream in = Sdk.class.getResourceAsStream(VERSION_RESOURCE)) {
            if (in == null) {
                return UNKNOWN_VERSION;
            }
            Properties properties = new Properties();
            properties.load(in);
            String version = properties.getProperty("version", "").trim();
            // An unfiltered placeholder means the resource was not processed by Maven (e.g. an IDE build).
            return version.isEmpty() || version.startsWith("${") ? UNKNOWN_VERSION : version;
        } catch (IOException e) {
            return UNKNOWN_VERSION;
        }
    }
}
