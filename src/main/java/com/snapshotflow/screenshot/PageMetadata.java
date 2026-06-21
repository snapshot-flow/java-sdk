package com.snapshotflow.screenshot;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Page metadata extracted during capture, returned when {@code metadata(true)} is requested. */
public final class PageMetadata {

    private String title;
    private String description;
    @JsonProperty("og_title")
    private String ogTitle;
    @JsonProperty("og_image")
    private String ogImage;
    @JsonProperty("og_description")
    private String ogDescription;
    private String favicon;
    @JsonProperty("http_status")
    private Integer httpStatus;

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String ogTitle() {
        return ogTitle;
    }

    public String ogImage() {
        return ogImage;
    }

    public String ogDescription() {
        return ogDescription;
    }

    public String favicon() {
        return favicon;
    }

    public Integer httpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return "PageMetadata{title=" + title + ", httpStatus=" + httpStatus + '}';
    }
}
