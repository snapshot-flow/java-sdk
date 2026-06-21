package com.snapshotflow.history;

import com.snapshotflow.screenshot.OutputFormat;

import java.util.Optional;

/** One row of a user's screenshot history ({@code GET /screenshots}). */
public final class ScreenshotSummary {

    private String screenshotId;
    private String sourceUrl;
    private OutputFormat format;
    private Integer width;
    private Integer height;
    private String createdAt;
    private String expiresAt;

    public String screenshotId() {
        return screenshotId;
    }

    /** Source URL captured, or empty for HTML captures. */
    public Optional<String> sourceUrl() {
        return Optional.ofNullable(sourceUrl);
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

    public String createdAt() {
        return createdAt;
    }

    /** When the stored file expires and the row is purged. */
    public String expiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return "ScreenshotSummary{screenshotId=" + screenshotId + ", sourceUrl=" + sourceUrl + '}';
    }
}
