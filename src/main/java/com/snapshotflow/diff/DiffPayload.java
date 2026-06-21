package com.snapshotflow.diff;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Internal DTO for the JSON body of {@code GET /diff} ({@code response_type=json|base64}). */
final class DiffPayload {

    String before;
    String after;
    Integer width;
    Integer height;
    @JsonProperty("changed_pixels")
    Long changedPixels;
    @JsonProperty("total_pixels")
    Long totalPixels;
    @JsonProperty("diff_percent")
    Double diffPercent;
    @JsonProperty("has_changes")
    Boolean hasChanges;
    String image;
}
