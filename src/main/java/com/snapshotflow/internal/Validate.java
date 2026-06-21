package com.snapshotflow.internal;

/** Tiny argument-checking helpers. Throw {@link IllegalArgumentException} on programmer error. */
public final class Validate {

    private Validate() {
    }

    public static <T> T notNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

    public static String notBlank(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    public static int inRange(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max + " (was " + value + ")");
        }
        return value;
    }
}
