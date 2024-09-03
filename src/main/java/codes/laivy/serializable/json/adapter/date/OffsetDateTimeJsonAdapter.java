package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.TestJson;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A JSON adapter for serializing and deserializing {@link OffsetDateTime} objects to and from JSON
 * using {@link JsonElement}. This adapter provides a mechanism to format and parse {@link OffsetDateTime}
 * using a {@link DateTimeFormatter}.
 *
 * <p>The {@link OffsetDateTimeJsonAdapter} class implements the {@link JsonAdapter} interface for
 * {@link OffsetDateTime}, allowing for conversion between {@link OffsetDateTime} instances and their
 * JSON string representations. It supports customization of the serialization format through
 * {@link DateTimeFormatter}.</p>
 *
 * <p>By default, this adapter uses {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}, which adheres
 * to the ISO-8601 standard for date-time representation, including the date, time, and time zone offset
 * (e.g., "2024-09-02T12:30:45+01:00"). This standard format is widely recognized and used in various
 * applications.</p>
 *
 * <p>A custom {@link DateTimeFormatter} can also be provided to accommodate different formatting
 * requirements. This allows for flexible representation of {@link OffsetDateTime} objects in JSON,
 * including variations in date and time patterns or different time zone notations.</p>
 *
 * <p>During serialization, the {@link OffsetDateTime} is formatted into a string according to the
 * specified {@link DateTimeFormatter}. The formatted string is then wrapped in a {@link JsonPrimitive}
 * for inclusion in the JSON output.</p>
 *
 * <p>During deserialization, the JSON string is parsed back into an {@link OffsetDateTime} object
 * using the specified {@link DateTimeFormatter}. The input JSON string must match the format defined
 * by the formatter; otherwise, an {@link InvalidClassException} will be thrown.</p>
 *
 * <p>In the event that the JSON element is null or not in the expected format, the adapter handles
 * these cases gracefully, returning null or throwing an {@link IllegalArgumentException} if the format
 * is incorrect.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public final class OffsetDateTimeJsonAdapter implements JsonAdapter<OffsetDateTime> {

    private final @NotNull DateTimeFormatter formatter;

    /**
     * Constructs an {@code OffsetDateTimeJsonAdapter} that uses the default {@link DateTimeFormatter}
     * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}. This formatter adheres to the ISO-8601 standard,
     * providing a widely used and compact representation for {@link OffsetDateTime}.
     */
    public OffsetDateTimeJsonAdapter() {
        this.formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    /**
     * Constructs an {@code OffsetDateTimeJsonAdapter} with a custom {@link DateTimeFormatter}.
     *
     * <p>The custom formatter allows for defining a specific pattern for serializing and deserializing
     * {@link OffsetDateTime}. This enables customization of the date and time format to meet various
     * requirements or preferences.</p>
     *
     * @param formatter The {@link DateTimeFormatter} to be used for formatting and parsing {@link OffsetDateTime}.
     *                  Must not be null and should match the format expected for {@link OffsetDateTime} objects.
     */
    public OffsetDateTimeJsonAdapter(@NotNull DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    // Getters

    /**
     * Returns the class reference for {@link OffsetDateTime}.
     *
     * @return A non-null {@link Class} object representing {@link OffsetDateTime}. This reference
     *         is used to identify the type handled by this adapter.
     */
    @Override
    public @NotNull Class<OffsetDateTime> getReference() {
        return OffsetDateTime.class;
    }

    /**
     * Returns the {@link DateTimeFormatter} used by this adapter for formatting and parsing {@link OffsetDateTime}.
     *
     * @return The non-null {@link DateTimeFormatter} instance that defines the serialization and deserialization
     *         format for {@link OffsetDateTime}.
     */
    public @NotNull DateTimeFormatter getFormatter() {
        return formatter;
    }

    // Serializers

    /**
     * Serializes an {@link OffsetDateTime} object into a {@link JsonElement}.
     *
     * <p>If the {@link OffsetDateTime} object is null, the method returns null. Otherwise, the {@link OffsetDateTime}
     * is formatted into a string using the specified {@link DateTimeFormatter}. The formatted string is then
     * converted into a {@link JsonPrimitive} for inclusion in the JSON output.</p>
     *
     * @param serializer The serializer instance.
     * @param instance The {@link OffsetDateTime} to be serialized, which may be null.
     * @return A {@link JsonPrimitive} containing the serialized string representation of the {@link OffsetDateTime},
     *         or null if the input {@link OffsetDateTime} was null.
     * @throws InvalidClassException If the {@link OffsetDateTime} cannot be serialized by this adapter.
     */
    @Override
    public @Nullable JsonElement serialize(@NotNull TestJson serializer, @Nullable OffsetDateTime instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.format(getFormatter()));
        }
    }

    /**
     * Deserializes a {@link JsonElement} into an {@link OffsetDateTime} object.
     *
     * <p>If the JSON element is null or JSON null, the method returns null. If the JSON element is a
     * string, it is parsed back into an {@link OffsetDateTime} using the specified {@link DateTimeFormatter}.
     * The format of the string must match the pattern defined by the formatter; otherwise, an
     * {@link InvalidClassException} will be thrown.</p>
     *
     * @param serializer The serializer instance.
     * @param json The {@link JsonElement} to be deserialized, which may be null.
     * @param reference The {@link OffsetDateTime} reference class.
     * @return The deserialized {@link OffsetDateTime} object, or null if the input JSON element was null.
     * @throws InvalidClassException If the JSON element cannot be parsed into an {@link OffsetDateTime}.
     * @throws IllegalArgumentException If the JSON element does not match the expected format for {@link OffsetDateTime}.
     */
    @Override
    public @Nullable OffsetDateTime deserialize(@NotNull TestJson serializer, @NotNull Class<OffsetDateTime> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return OffsetDateTime.parse(json.getAsString(), getFormatter());
        }
    }
}