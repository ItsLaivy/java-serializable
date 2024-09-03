package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.JsonSerializable;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A JSON adapter for serializing and deserializing {@link LocalDateTime} objects to and from JSON
 * using {@link JsonElement}. This adapter provides functionality to format and parse {@link LocalDateTime}
 * using a {@link DateTimeFormatter}.
 *
 * <p>The {@link LocalDateTimeJsonAdapter} class implements the {@link JsonAdapter} interface for
 * {@link LocalDateTime}, enabling conversion between {@link LocalDateTime} instances and their JSON
 * string representations. It supports customizable serialization formats via {@link DateTimeFormatter}.</p>
 *
 * <p>By default, this adapter uses {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}, which is a standard
 * format for representing date-time without any time zone information, as per the ISO-8601 standard
 * (e.g., "2024-09-02T12:30:45"). This format includes date and time up to nanosecond precision, but
 * excludes any time zone or offset information.</p>
 *
 * <p>A custom {@link DateTimeFormatter} can be provided during construction to allow for different
 * serialization formats. This flexibility is useful for aligning the date-time representation with
 * specific application needs or standards.</p>
 *
 * <p>During serialization, the {@link LocalDateTime} object is formatted into a string according to
 * the specified {@link DateTimeFormatter}. The formatted string is then wrapped in a {@link JsonPrimitive}
 * for inclusion in the JSON output.</p>
 *
 * <p>During deserialization, the JSON string is parsed back into a {@link LocalDateTime} object using
 * the provided {@link DateTimeFormatter}. The JSON string must adhere to the format defined by the
 * formatter; otherwise, an {@link InvalidClassException} will be thrown.</p>
 *
 * <p>If the JSON element is null or not in the expected format, the adapter handles these cases gracefully.
 * If the JSON element is null, the method will return null. If the format does not match, an
 * {@link InvalidClassException} will be thrown during parsing.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public final class LocalDateTimeJsonAdapter implements JsonAdapter<LocalDateTime> {

    private final @NotNull DateTimeFormatter formatter;

    /**
     * Constructs a {@code LocalDateTimeJsonAdapter} using the default {@link DateTimeFormatter}
     * {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}. This formatter provides a standard representation
     * for {@link LocalDateTime} without any time zone information.
     */
    public LocalDateTimeJsonAdapter() {
        this.formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    /**
     * Constructs a {@code LocalDateTimeJsonAdapter} with a custom {@link DateTimeFormatter}.
     *
     * <p>The custom formatter allows you to define a specific pattern for serializing and deserializing
     * {@link LocalDateTime}. This flexibility enables different formatting styles as needed.</p>
     *
     * @param formatter The {@link DateTimeFormatter} to be used for formatting and parsing {@link LocalDateTime}.
     *                  Must not be null and should be compatible with {@link LocalDateTime} objects.
     */
    public LocalDateTimeJsonAdapter(@NotNull DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    // Getters

    /**
     * Returns the class reference for {@link LocalDateTime}.
     *
     * @return A non-null {@link Class} object representing {@link LocalDateTime}. This reference
     *         is used to identify the type handled by this adapter.
     */
    @Override
    public @NotNull Class<LocalDateTime> getReference() {
        return LocalDateTime.class;
    }

    /**
     * Returns the {@link DateTimeFormatter} used by this adapter for formatting and parsing {@link LocalDateTime}.
     *
     * @return The non-null {@link DateTimeFormatter} instance that defines the serialization and deserialization
     *         format for {@link LocalDateTime}.
     */
    public @NotNull DateTimeFormatter getFormatter() {
        return formatter;
    }

    // Serializers

    /**
     * Serializes a {@link LocalDateTime} object into a {@link JsonElement}.
     *
     * <p>If the {@link LocalDateTime} object is null, the method returns null. Otherwise, the {@link LocalDateTime}
     * is formatted into a string using the specified {@link DateTimeFormatter}. The formatted string is then
     * converted into a {@link JsonPrimitive} for inclusion in the JSON output.</p>
     *
     * @param serializer The serializer instance.
     * @param instance The {@link LocalDateTime} to be serialized, which may be null.
     * @return A {@link JsonPrimitive} containing the serialized string representation of the {@link LocalDateTime},
     *         or null if the input {@link LocalDateTime} was null.
     * @throws InvalidClassException If the {@link LocalDateTime} cannot be serialized by this adapter.
     */
    @Override
    public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable LocalDateTime instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.format(getFormatter()));
        }
    }

    /**
     * Deserializes a {@link JsonElement} into a {@link LocalDateTime} object.
     *
     * <p>If the JSON element is null or JSON null, the method returns null. If the JSON element is a
     * string, it is parsed back into a {@link LocalDateTime} using the specified {@link DateTimeFormatter}.
     * The format of the string must match the pattern defined by the formatter; otherwise, an
     * {@link InvalidClassException} will be thrown.</p>
     *
     * @param serializer The serializer instance.
     * @param json The {@link JsonElement} to be deserialized, which may be null.
     * @param reference The {@link LocalDateTime} reference class.
     * @return The deserialized {@link LocalDateTime} object, or null if the input JSON element was null.
     * @throws InvalidClassException If the JSON element cannot be parsed into a {@link LocalDateTime}.
     * @throws IllegalArgumentException If the JSON element does not match the expected format for {@link LocalDateTime}.
     */
    @Override
    public @Nullable LocalDateTime deserialize(@NotNull JsonSerializable serializer, @NotNull Class<LocalDateTime> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return LocalDateTime.parse(json.getAsString(), getFormatter());
        }
    }
}