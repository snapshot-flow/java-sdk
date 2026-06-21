package com.snapshotflow.internal;

import java.util.LinkedHashMap;
import java.util.Map;

/** An outgoing API call: method, path, query, optional body and per-call headers. Built fluently. */
public final class ApiRequest {

    public enum Method {
        GET, POST, DELETE
    }

    private final Method method;
    private final String path;
    private final QueryParams query;
    private final byte[] body;
    private final String contentType;
    private final Map<String, String> headers;
    private final boolean idempotent;

    private ApiRequest(Builder b) {
        this.method = b.method;
        this.path = b.path;
        this.query = b.query;
        this.body = b.body;
        this.contentType = b.contentType;
        this.headers = b.headers;
        this.idempotent = b.idempotent;
    }

    public Method method() {
        return method;
    }

    public String path() {
        return path;
    }

    public QueryParams query() {
        return query;
    }

    public byte[] body() {
        return body;
    }

    public String contentType() {
        return contentType;
    }

    public Map<String, String> headers() {
        return headers;
    }

    /** Whether re-sending after a transient failure is safe. GET defaults to {@code true}, others to {@code false}. */
    public boolean idempotent() {
        return idempotent;
    }

    public static Builder get(String path) {
        return new Builder(Method.GET, path).idempotent(true);
    }

    public static Builder post(String path) {
        return new Builder(Method.POST, path);
    }

    public static Builder delete(String path) {
        return new Builder(Method.DELETE, path);
    }

    public static final class Builder {
        private final Method method;
        private final String path;
        private QueryParams query = new QueryParams();
        private byte[] body;
        private String contentType;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private boolean idempotent;

        private Builder(Method method, String path) {
            this.method = method;
            this.path = path;
        }

        public Builder query(QueryParams query) {
            this.query = query == null ? new QueryParams() : query;
            return this;
        }

        public Builder jsonBody(Object value) {
            this.body = Json.writeBytes(value);
            this.contentType = "application/json";
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder idempotent(boolean idempotent) {
            this.idempotent = idempotent;
            return this;
        }

        public ApiRequest build() {
            return new ApiRequest(this);
        }
    }
}
