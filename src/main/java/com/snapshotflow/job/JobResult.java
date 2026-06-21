package com.snapshotflow.job;

import com.snapshotflow.screenshot.OutputFormat;

import java.util.Optional;

/** The {@code result} block of a finished {@link Job}. */
public final class JobResult {

    private String storagePath;
    private String url;
    private OutputFormat format;
    private Integer width;
    private Integer height;
    private String screenshotId;
    private Long sizeBytes;

    /** Signed download URL for the rendered file (time-limited — fetch promptly). */
    public Optional<String> storagePath() {
        return Optional.ofNullable(storagePath);
    }

    public String url() {
        return url;
    }

    public OutputFormat format() {
        return format;
    }

    public Integer width() {
        return width;
    }

    public Integer height() {
        return height;
    }

    public Optional<String> screenshotId() {
        return Optional.ofNullable(screenshotId);
    }

    public Optional<Long> sizeBytes() {
        return Optional.ofNullable(sizeBytes);
    }
}
