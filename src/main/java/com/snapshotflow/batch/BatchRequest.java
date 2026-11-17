package com.snapshotflow.batch;

import com.snapshotflow.internal.Validate;
import com.snapshotflow.screenshot.OutputFormat;
import com.snapshotflow.screenshot.WaitUntil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Up to {@value #MAX_URLS} URLs captured in one {@code POST /batch} call with shared options. */
public final class BatchRequest {

    public static final int MAX_URLS = 10;

    private final List<String> urls;
    private final OutputFormat format;
    private final Integer quality;
    private final Integer width;
    private final Integer height;
    private final Boolean fullPage;
    private final Boolean blockAds;
    private final Boolean blockCookieBanners;
    private final WaitUntil waitUntil;
    private final BatchResponseType responseType;

    private BatchRequest(Builder b) {
        this.urls = b.urls;
        this.format = b.format;
        this.quality = b.quality;
        this.width = b.width;
        this.height = b.height;
        this.fullPage = b.fullPage;
        this.blockAds = b.blockAds;
        this.blockCookieBanners = b.blockCookieBanners;
        this.waitUntil = b.waitUntil;
        this.responseType = b.responseType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder urls(String... urls) {
        return new Builder().urls(urls);
    }

    public static Builder urls(List<String> urls) {
        return new Builder().urls(urls);
    }

    Map<String, Object> toBody() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("urls", urls);
        if (format != null) {
            body.put("format", format.wire());
        }
        if (quality != null) {
            body.put("quality", quality);
        }
        if (width != null) {
            body.put("width", width);
        }
        if (height != null) {
            body.put("height", height);
        }
        if (fullPage != null) {
            body.put("full_page", fullPage);
        }
        if (blockAds != null) {
            body.put("block_ads", blockAds);
        }
        if (blockCookieBanners != null) {
            body.put("block_cookie_banners", blockCookieBanners);
        }
        if (waitUntil != null) {
            body.put("wait_until", waitUntil.wire());
        }
        if (responseType != null) {
            body.put("response_type", responseType.wire());
        }
        return body;
    }

    Map<String, Object> toAsyncBody() {
        Map<String, Object> body = toBody();
        body.put("async", Boolean.TRUE);
        return body;
    }

    public static final class Builder {
        private List<String> urls = new ArrayList<>();
        private OutputFormat format;
        private Integer quality;
        private Integer width;
        private Integer height;
        private Boolean fullPage;
        private Boolean blockAds;
        private Boolean blockCookieBanners;
        private WaitUntil waitUntil;
        private BatchResponseType responseType;

        public Builder urls(String... urls) {
            this.urls = new ArrayList<>(Arrays.asList(urls));
            return this;
        }

        public Builder urls(List<String> urls) {
            this.urls = new ArrayList<>(urls);
            return this;
        }

        public Builder addUrl(String url) {
            this.urls.add(url);
            return this;
        }

        public Builder format(OutputFormat format) {
            this.format = format;
            return this;
        }

        public Builder quality(int quality) {
            this.quality = quality;
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

        public Builder fullPage(boolean fullPage) {
            this.fullPage = fullPage;
            return this;
        }

        public Builder blockAds(boolean blockAds) {
            this.blockAds = blockAds;
            return this;
        }

        public Builder blockCookieBanners(boolean blockCookieBanners) {
            this.blockCookieBanners = blockCookieBanners;
            return this;
        }

        public Builder waitUntil(WaitUntil waitUntil) {
            this.waitUntil = waitUntil;
            return this;
        }

        public Builder responseType(BatchResponseType responseType) {
            this.responseType = responseType;
            return this;
        }

        public BatchRequest build() {
            Validate.notNull(urls, "urls");
            if (urls.isEmpty()) {
                throw new IllegalArgumentException("batch requires at least one url");
            }
            if (urls.size() > MAX_URLS) {
                throw new IllegalArgumentException("batch accepts at most " + MAX_URLS + " urls (got " + urls.size() + ")");
            }
            return new BatchRequest(this);
        }
    }
}
