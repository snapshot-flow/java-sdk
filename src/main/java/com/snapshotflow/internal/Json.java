package com.snapshotflow.internal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapshotflow.exception.SnapshotFlowException;

import java.io.IOException;

/**
 * Central JSON (de)serialization for the SDK. Wraps a single, shared,
 * thread-safe Jackson {@link ObjectMapper}. Every JSON touchpoint goes through
 * here so the dependency stays isolated and replaceable.
 *
 * <p>Configured to ignore unknown properties — the API may add response fields
 * without breaking older SDK versions — and to omit {@code null} fields when
 * serializing request bodies.
 */
public final class Json {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            // Bind purely by field so model classes can stay as private fields + getters,
            // with no setters, no-arg constructors or @JsonCreator boilerplate. Explicit
            // annotations (@JsonValue / @JsonProperty / @JsonCreator on enums) still win.
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);

    private Json() {
    }

    /** The shared mapper, exposed for advanced callers. Treat its configuration as read-only. */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static <T> T read(byte[] body, Class<T> type) {
        try {
            return MAPPER.readValue(body, type);
        } catch (IOException e) {
            throw new SnapshotFlowException("Failed to parse API response as " + type.getSimpleName(), e);
        }
    }

    public static <T> T read(String body, Class<T> type) {
        try {
            return MAPPER.readValue(body, type);
        } catch (IOException e) {
            throw new SnapshotFlowException("Failed to parse JSON as " + type.getSimpleName(), e);
        }
    }

    public static JsonNode readTree(byte[] body) {
        try {
            return MAPPER.readTree(body);
        } catch (IOException e) {
            throw new SnapshotFlowException("Failed to parse API response as JSON", e);
        }
    }

    public static byte[] writeBytes(Object value) {
        try {
            return MAPPER.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new SnapshotFlowException("Failed to serialize request body", e);
        }
    }
}
