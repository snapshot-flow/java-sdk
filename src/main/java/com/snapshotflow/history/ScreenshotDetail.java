package com.snapshotflow.history;

import com.snapshotflow.screenshot.OutputFormat;

import java.util.Optional;

/** A single screenshot with a freshly signed download URL ({@code GET /screenshots/:id}). */
public final class ScreenshotDetail {

    private String screenshotId;
    private String sourceUrl;
    private OutputFormat format;
    private Integer width;
    private Integer height;
    private String createdAt;
    private String expiresAt;
    private String downloadUrl;
    private String downloadUrlExpiresAt;

    public String screenshotId() {
        return screenshotId;
    }

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

    public String expiresAt() {
        return expiresAt;
    }

    /** Time-limited signed URL to download the stored file. */
    public String downloadUrl() {
        return downloadUrl;
    }

    /** When {@link #downloadUrl()} stops working. */
    public String downloadUrlExpiresAt() {
        return downloadUrlExpiresAt;
    }

    @Override
    public String toString() {
        return "ScreenshotDetail{screenshotId=" + screenshotId + ", downloadUrl=" + downloadUrl + '}';
    }
}
