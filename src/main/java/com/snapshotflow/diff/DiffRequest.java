package com.snapshotflow.diff;

import com.snapshotflow.internal.QueryParams;
import com.snapshotflow.internal.Validate;
import com.snapshotflow.screenshot.ResponseType;

/** A visual diff between two URLs ({@code GET /diff}). */
public final class DiffRequest {

    private final String before;
    private final String after;
    private final Integer width;
    private final Integer height;
    private final Double threshold;
    private final ResponseType responseType;

    private DiffRequest(Builder b) {
        this.before = b.before;
        this.after = b.after;
        this.width = b.width;
        this.height = b.height;
        this.threshold = b.threshold;
        this.responseType = b.responseType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder of(String before, String after) {
        return new Builder().before(before).after(after);
    }

    ResponseType responseType() {
        return responseType;
    }

    QueryParams toQuery(ResponseType effectiveResponseType) {
        QueryParams q = new QueryParams();
        q.add("before", before);
        q.add("after", after);
        q.add("width", width);
        q.add("height", height);
        q.add("threshold", threshold);
        q.add("response_type", effectiveResponseType.wire());
        return q;
    }

    public static final class Builder {
        private String before;
        private String after;
        private Integer width;
        private Integer height;
        private Double threshold;
        private ResponseType responseType;

        /** URL of the baseline ("before") page. Required. */
        public Builder before(String before) {
            this.before = before;
            return this;
        }

        /** URL of the comparison ("after") page. Required. */
        public Builder after(String after) {
            this.after = after;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder viewport(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /** Matching threshold, 0–1 (lower = more sensitive). */
        public Builder threshold(double threshold) {
            this.threshold = threshold;
            return this;
        }

        /** Override the response shape. Defaults to base64 (stats + diff image together). */
        public Builder responseType(ResponseType responseType) {
            this.responseType = responseType;
            return this;
        }

        public DiffRequest build() {
            Validate.notBlank(before, "before");
            Validate.notBlank(after, "after");
            return new DiffRequest(this);
        }
    }
}
