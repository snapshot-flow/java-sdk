package com.snapshotflow.extract;

import com.snapshotflow.internal.QueryParams;
import com.snapshotflow.internal.Validate;
import com.snapshotflow.screenshot.ContentFormat;

/** Request parameters for content extraction ({@code GET /extract}). */
public final class ExtractRequest {

    private final String url;
    private final ContentFormat format;
    private final Integer maxChars;

    private ExtractRequest(Builder b) {
        this.url = b.url;
        this.format = b.format;
        this.maxChars = b.maxChars;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder url(String url) {
        return new Builder().url(url);
    }

    QueryParams toQuery() {
        QueryParams q = new QueryParams();
        q.add("url", url);
        q.add("format", format == null ? null : format.wire());
        q.add("max_chars", maxChars);
        return q;
    }

    public static final class Builder {
        private String url;
        private ContentFormat format;
        private Integer maxChars;

        /** Page URL to extract content from (http/https). Required. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Output format ({@code markdown}, {@code html}, or {@code text}). Server defaults to {@code markdown}. */
        public Builder format(ContentFormat format) {
            this.format = format;
            return this;
        }

        /** Maximum character count to return (100–500000). */
        public Builder maxChars(int maxChars) {
            this.maxChars = maxChars;
            return this;
        }

        public ExtractRequest build() {
            Validate.notBlank(url, "url");
            return new ExtractRequest(this);
        }
    }
}
