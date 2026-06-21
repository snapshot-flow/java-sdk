package com.snapshotflow.screenshot;

import com.snapshotflow.internal.Json;
import com.snapshotflow.internal.QueryParams;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Immutable description of a capture. Build one with {@link #url(String)},
 * {@link #html(String)} or {@link #builder()} and the fluent setters; only the
 * parameters you set are sent, so the server applies its own defaults for the
 * rest.
 *
 * <pre>{@code
 * ScreenshotRequest req = ScreenshotRequest.url("https://example.com")
 *         .fullPage(true)
 *         .format(OutputFormat.JPEG)
 *         .quality(90)
 *         .darkMode(true)
 *         .build();
 * }</pre>
 *
 * The same request is accepted by both {@code client.screenshots().capture(req)}
 * (synchronous) and {@code client.screenshots().submit(req)} (async job); the
 * {@code response_type} and {@code async} parameters are managed by those
 * methods, not set here.
 */
public final class ScreenshotRequest {

    private final String url;
    private final String rawHtml;
    private final Integer width;
    private final Integer height;
    private final Double deviceScaleFactor;
    private final Boolean viewportMobile;
    private final OutputFormat format;
    private final Integer quality;
    private final Boolean fullPage;
    private final Boolean omitBackground;
    private final String selector;
    private final Integer clipX;
    private final Integer clipY;
    private final Integer clipWidth;
    private final Integer clipHeight;
    private final Integer delayMs;
    private final WaitUntil waitUntil;
    private final String waitForSelector;
    private final Boolean darkMode;
    private final Boolean reducedMotion;
    private final MediaType mediaType;
    private final String timezone;
    private final Double geolocationLatitude;
    private final Double geolocationLongitude;
    private final Double geolocationAccuracy;
    private final String userAgent;
    private final String headers;
    private final String cookies;
    private final String scripts;
    private final String styles;
    private final String hideSelectors;
    private final String click;
    private final Integer imageWidth;
    private final Integer imageHeight;
    private final Boolean pdfPrintBackground;
    private final Boolean pdfLandscape;
    private final PdfPaperFormat pdfPaperFormat;
    private final Boolean blockAds;
    private final Boolean blockTrackers;
    private final Boolean blockCookieBanners;
    private final String blockRequests;
    private final Boolean extractContent;
    private final ContentFormat contentFormat;
    private final Boolean metadata;
    private final Boolean cache;
    private final String webhookUrl;
    private final String externalIdentifier;
    private final Boolean webhookErrors;

    private ScreenshotRequest(Builder b) {
        this.url = b.url;
        this.rawHtml = b.rawHtml;
        this.width = b.width;
        this.height = b.height;
        this.deviceScaleFactor = b.deviceScaleFactor;
        this.viewportMobile = b.viewportMobile;
        this.format = b.format;
        this.quality = b.quality;
        this.fullPage = b.fullPage;
        this.omitBackground = b.omitBackground;
        this.selector = b.selector;
        this.clipX = b.clipX;
        this.clipY = b.clipY;
        this.clipWidth = b.clipWidth;
        this.clipHeight = b.clipHeight;
        this.delayMs = b.delayMs;
        this.waitUntil = b.waitUntil;
        this.waitForSelector = b.waitForSelector;
        this.darkMode = b.darkMode;
        this.reducedMotion = b.reducedMotion;
        this.mediaType = b.mediaType;
        this.timezone = b.timezone;
        this.geolocationLatitude = b.geolocationLatitude;
        this.geolocationLongitude = b.geolocationLongitude;
        this.geolocationAccuracy = b.geolocationAccuracy;
        this.userAgent = b.userAgent;
        this.headers = b.headers;
        this.cookies = b.cookies;
        this.scripts = b.scripts;
        this.styles = b.styles;
        this.hideSelectors = b.hideSelectors;
        this.click = b.click;
        this.imageWidth = b.imageWidth;
        this.imageHeight = b.imageHeight;
        this.pdfPrintBackground = b.pdfPrintBackground;
        this.pdfLandscape = b.pdfLandscape;
        this.pdfPaperFormat = b.pdfPaperFormat;
        this.blockAds = b.blockAds;
        this.blockTrackers = b.blockTrackers;
        this.blockCookieBanners = b.blockCookieBanners;
        this.blockRequests = b.blockRequests;
        this.extractContent = b.extractContent;
        this.contentFormat = b.contentFormat;
        this.metadata = b.metadata;
        this.cache = b.cache;
        this.webhookUrl = b.webhookUrl;
        this.externalIdentifier = b.externalIdentifier;
        this.webhookErrors = b.webhookErrors;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Start a request that captures the given page URL. */
    public static Builder url(String url) {
        return new Builder().url(url);
    }

    /** Start a request that renders the given raw HTML (base64-encoded for you on the wire). */
    public static Builder html(String rawHtml) {
        return new Builder().html(rawHtml);
    }

    boolean wantsJsonResponse() {
        return Boolean.TRUE.equals(metadata) || Boolean.TRUE.equals(extractContent);
    }

    OutputFormat formatOrDefault() {
        return format != null ? format : OutputFormat.PNG;
    }

    String webhookUrl() {
        return webhookUrl;
    }

    String externalIdentifier() {
        return externalIdentifier;
    }

    /** Build the query for every user-set parameter except {@code response_type} / {@code async}. */
    QueryParams toQuery() {
        QueryParams q = new QueryParams();
        q.add("url", url);
        if (rawHtml != null) {
            q.add("html", Base64.getEncoder().encodeToString(rawHtml.getBytes(StandardCharsets.UTF_8)));
        }
        q.add("width", width);
        q.add("height", height);
        q.add("device_scale_factor", deviceScaleFactor);
        q.add("viewport_mobile", viewportMobile);
        q.add("format", format == null ? null : format.wire());
        q.add("quality", quality);
        q.add("full_page", fullPage);
        q.add("omit_background", omitBackground);
        q.add("selector", selector);
        q.add("clip_x", clipX);
        q.add("clip_y", clipY);
        q.add("clip_width", clipWidth);
        q.add("clip_height", clipHeight);
        q.add("delay", delayMs);
        q.add("wait_until", waitUntil == null ? null : waitUntil.wire());
        q.add("wait_for_selector", waitForSelector);
        q.add("dark_mode", darkMode);
        q.add("reduced_motion", reducedMotion);
        q.add("media_type", mediaType == null ? null : mediaType.wire());
        q.add("timezone", timezone);
        q.add("geolocation_latitude", geolocationLatitude);
        q.add("geolocation_longitude", geolocationLongitude);
        q.add("geolocation_accuracy", geolocationAccuracy);
        q.add("user_agent", userAgent);
        q.add("headers", headers);
        q.add("cookies", cookies);
        q.add("scripts", scripts);
        q.add("styles", styles);
        q.add("hide_selectors", hideSelectors);
        q.add("click", click);
        q.add("image_width", imageWidth);
        q.add("image_height", imageHeight);
        q.add("pdf_print_background", pdfPrintBackground);
        q.add("pdf_landscape", pdfLandscape);
        q.add("pdf_paper_format", pdfPaperFormat == null ? null : pdfPaperFormat.wire());
        q.add("block_ads", blockAds);
        q.add("block_trackers", blockTrackers);
        q.add("block_cookie_banners", blockCookieBanners);
        q.add("block_requests", blockRequests);
        q.add("extract_content", extractContent);
        q.add("content_format", contentFormat == null ? null : contentFormat.wire());
        q.add("metadata", metadata);
        q.add("cache", cache);
        q.add("webhook_url", webhookUrl);
        q.add("external_identifier", externalIdentifier);
        q.add("webhook_errors", webhookErrors);
        return q;
    }

    /** Fluent builder for {@link ScreenshotRequest}. All setters are optional unless noted. */
    public static final class Builder {
        private String url;
        private String rawHtml;
        private Integer width;
        private Integer height;
        private Double deviceScaleFactor;
        private Boolean viewportMobile;
        private OutputFormat format;
        private Integer quality;
        private Boolean fullPage;
        private Boolean omitBackground;
        private String selector;
        private Integer clipX;
        private Integer clipY;
        private Integer clipWidth;
        private Integer clipHeight;
        private Integer delayMs;
        private WaitUntil waitUntil;
        private String waitForSelector;
        private Boolean darkMode;
        private Boolean reducedMotion;
        private MediaType mediaType;
        private String timezone;
        private Double geolocationLatitude;
        private Double geolocationLongitude;
        private Double geolocationAccuracy;
        private String userAgent;
        private String headers;
        private String cookies;
        private String scripts;
        private String styles;
        private String hideSelectors;
        private String click;
        private Integer imageWidth;
        private Integer imageHeight;
        private Boolean pdfPrintBackground;
        private Boolean pdfLandscape;
        private PdfPaperFormat pdfPaperFormat;
        private Boolean blockAds;
        private Boolean blockTrackers;
        private Boolean blockCookieBanners;
        private String blockRequests;
        private Boolean extractContent;
        private ContentFormat contentFormat;
        private Boolean metadata;
        private Boolean cache;
        private String webhookUrl;
        private String externalIdentifier;
        private Boolean webhookErrors;

        /** Page URL to capture (http/https). Required unless {@link #html(String)} is set. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Raw HTML to render instead of navigating to a URL. Base64-encoded for you. */
        public Builder html(String rawHtml) {
            this.rawHtml = rawHtml;
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

        public Builder deviceScaleFactor(double factor) {
            this.deviceScaleFactor = factor;
            return this;
        }

        public Builder viewportMobile(boolean mobile) {
            this.viewportMobile = mobile;
            return this;
        }

        public Builder format(OutputFormat format) {
            this.format = format;
            return this;
        }

        /** JPEG/WebP quality, 1–100. */
        public Builder quality(int quality) {
            this.quality = quality;
            return this;
        }

        public Builder fullPage(boolean fullPage) {
            this.fullPage = fullPage;
            return this;
        }

        public Builder omitBackground(boolean omitBackground) {
            this.omitBackground = omitBackground;
            return this;
        }

        /** CSS selector to clip the screenshot to a single element. */
        public Builder selector(String selector) {
            this.selector = selector;
            return this;
        }

        public Builder clip(int x, int y, int width, int height) {
            this.clipX = x;
            this.clipY = y;
            this.clipWidth = width;
            this.clipHeight = height;
            return this;
        }

        /** Extra wait after load, in milliseconds (0–10000). */
        public Builder delay(int millis) {
            this.delayMs = millis;
            return this;
        }

        public Builder waitUntil(WaitUntil waitUntil) {
            this.waitUntil = waitUntil;
            return this;
        }

        public Builder waitForSelector(String selector) {
            this.waitForSelector = selector;
            return this;
        }

        public Builder darkMode(boolean darkMode) {
            this.darkMode = darkMode;
            return this;
        }

        public Builder reducedMotion(boolean reducedMotion) {
            this.reducedMotion = reducedMotion;
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder geolocation(double latitude, double longitude) {
            this.geolocationLatitude = latitude;
            this.geolocationLongitude = longitude;
            return this;
        }

        public Builder geolocationAccuracy(double accuracy) {
            this.geolocationAccuracy = accuracy;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /** Extra request headers, as a JSON object string (e.g. {@code {"X-Foo":"bar"}}). */
        public Builder headers(String headersJson) {
            this.headers = headersJson;
            return this;
        }

        /** Extra request headers as a map — serialized to JSON for you. */
        public Builder headers(Map<String, String> headers) {
            this.headers = headers == null ? null : new String(Json.writeBytes(headers), StandardCharsets.UTF_8);
            return this;
        }

        /** Cookies to set, in the format the API expects (JSON string). */
        public Builder cookies(String cookies) {
            this.cookies = cookies;
            return this;
        }

        /** JavaScript to inject before capture. */
        public Builder scripts(String scripts) {
            this.scripts = scripts;
            return this;
        }

        /** CSS to inject before capture. */
        public Builder styles(String styles) {
            this.styles = styles;
            return this;
        }

        /** Comma-separated selectors to hide before capture. */
        public Builder hideSelectors(String hideSelectors) {
            this.hideSelectors = hideSelectors;
            return this;
        }

        /** CSS selector to click before capture. */
        public Builder click(String click) {
            this.click = click;
            return this;
        }

        /** Resize the rendered image to this width (post-processing). */
        public Builder imageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
            return this;
        }

        public Builder imageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
            return this;
        }

        public Builder pdfPrintBackground(boolean printBackground) {
            this.pdfPrintBackground = printBackground;
            return this;
        }

        public Builder pdfLandscape(boolean landscape) {
            this.pdfLandscape = landscape;
            return this;
        }

        public Builder pdfPaperFormat(PdfPaperFormat paperFormat) {
            this.pdfPaperFormat = paperFormat;
            return this;
        }

        public Builder blockAds(boolean blockAds) {
            this.blockAds = blockAds;
            return this;
        }

        public Builder blockTrackers(boolean blockTrackers) {
            this.blockTrackers = blockTrackers;
            return this;
        }

        public Builder blockCookieBanners(boolean blockCookieBanners) {
            this.blockCookieBanners = blockCookieBanners;
            return this;
        }

        /** Comma-separated URL patterns to block during load. */
        public Builder blockRequests(String blockRequests) {
            this.blockRequests = blockRequests;
            return this;
        }

        public Builder extractContent(boolean extractContent) {
            this.extractContent = extractContent;
            return this;
        }

        public Builder contentFormat(ContentFormat contentFormat) {
            this.contentFormat = contentFormat;
            return this;
        }

        /** Capture page metadata (title, description, OpenGraph, favicon, HTTP status). */
        public Builder metadata(boolean metadata) {
            this.metadata = metadata;
            return this;
        }

        /** Whether to use the server cache (default true). Set {@code false} to force a fresh capture. */
        public Builder cache(boolean cache) {
            this.cache = cache;
            return this;
        }

        /** Webhook URL to notify when an async job finishes (used by {@code submit}). */
        public Builder webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }

        /** Opaque id echoed back in webhook headers/payload for correlation (max 255 chars). */
        public Builder externalIdentifier(String externalIdentifier) {
            this.externalIdentifier = externalIdentifier;
            return this;
        }

        /** Whether failure webhooks include error details (default true). */
        public Builder webhookErrors(boolean webhookErrors) {
            this.webhookErrors = webhookErrors;
            return this;
        }

        public ScreenshotRequest build() {
            if (url == null && rawHtml == null) {
                throw new IllegalArgumentException("Either url(...) or html(...) must be set");
            }
            return new ScreenshotRequest(this);
        }
    }
}
