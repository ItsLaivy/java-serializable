package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.TestJson;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

/**
 * A JSON adapter for serializing and deserializing {@link OffsetTime} objects to and from JSON
 * using {@link JsonElement}. This adapter provides a way to customize the serialization format
 * of {@link OffsetTime} through the use of {@link DateTimeFormatter}.
 *
 * <p>The {@link OffsetTimeJsonAdapter} class implements the {@link JsonAdapter} interface for
 * {@link OffsetTime}. It allows for conversion between {@link OffsetTime} objects and their
 * JSON string representation. The class supports both the default ISO 8601 format and custom
 * formats specified by a {@link DateTimeFormatter}.</p>
 *
 * <p>The default formatter used by this adapter is {@link DateTimeFormatter#ISO_OFFSET_TIME},
 * which follows the ISO-8601 standard for representing offset times, such as "13:45:30+01:00".
 * This standard format includes hours, minutes, seconds, and the time zone offset from UTC.</p>
 *
 * <p>When a custom {@link DateTimeFormatter} is provided, the serialization and deserialization
 * will follow the pattern defined by that formatter. This allows for flexibility in how {@link OffsetTime}
 * values are represented in JSON, accommodating various date-time formatting requirements.</p>
 *
 * <p>The serialization process involves formatting the {@link OffsetTime} object into a string
 * using the specified {@link DateTimeFormatter}. The resulting string is then wrapped in a {@link JsonPrimitive}
 * for inclusion in the JSON output.</p>
 *
 * <p>During deserialization, the adapter parses the JSON string back into an {@link OffsetTime} object
 * using the same {@link DateTimeFormatter}. The string representation of the {@link OffsetTime} must
 * match the expected format defined by the formatter; otherwise, an {@link InvalidClassException} may be thrown.</p>
 *
 * <p>In case the JSON element is null or is not in the expected format, the adapter handles these cases
 * gracefully by returning null or throwing an {@link IllegalArgumentException} if the format is incorrect.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public final class OffsetTimeJsonAdapter implements JsonAdapter<OffsetTime> {

    private final @NotNull DateTimeFormatter formatter;

    /**
     * Constructs an {@code OffsetTimeJsonAdapter} that uses the default {@link DateTimeFormatter}
     * {@link DateTimeFormatter#ISO_OFFSET_TIME}. This formatter follows the ISO-8601 standard, which
     * represents {@link OffsetTime} in a compact and widely recognized format.
     */
    public OffsetTimeJsonAdapter() {
        this.formatter = DateTimeFormatter.ISO_OFFSET_TIME;
    }

    /**
     * Constructs an {@code OffsetTimeJsonAdapter} with a custom {@link DateTimeFormatter}.
     *
     * <p>The custom formatter allows for specifying a different pattern for serializing and deserializing
     * {@link OffsetTime}. For example, you can define patterns to include or exclude specific components
     * of the time, or to use different time zone representations.</p>
     *
     * @param formatter The {@link DateTimeFormatter} to be used for formatting and parsing {@link OffsetTime}.
     *                  It should not be null and must be compatible with the format of {@link OffsetTime}.
     */
    public OffsetTimeJsonAdapter(@NotNull DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    // Getters

    /**
     * Returns the class reference for {@link OffsetTime}.
     *
     * @return A non-null {@link Class} object representing {@link OffsetTime}. This is used to
     *         identify the type being handled by this adapter.
     */
    @Override
    public @NotNull Class<OffsetTime> getReference() {
        return OffsetTime.class;
    }

    /**
     * Returns the {@link DateTimeFormatter} used by this adapter for formatting and parsing {@link OffsetTime}.
     *
     * @return The non-null {@link DateTimeFormatter} instance that defines the format for serialization
     *         and deserialization processes.
     */
    public @NotNull DateTimeFormatter getFormatter() {
        return formatter;
    }

    // Serializers

    /**
     * Serializes an {@link OffsetTime} object into a {@link JsonElement}.
     *
     * <p>If the {@link OffsetTime} object is null, the method returns null. If the object is not null,
     * it is formatted into a string using the specified {@link DateTimeFormatter}. The formatted string
     * is then converted into a {@link JsonPrimitive}.</p>
     *
     * @param serializer The serializer instance.
     * @param instance The {@link OffsetTime} to be serialized, which may be null.
     * @return A {@link JsonPrimitive} containing the serialized string representation of the {@link OffsetTime},
     *         or null if the input {@link OffsetTime} was null.
     * @throws InvalidClassException If the {@link OffsetTime} cannot be serialized by this adapter.
     */
    @Override
    public @Nullable JsonElement serialize(@NotNull TestJson serializer, @Nullable OffsetTime instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.format(getFormatter()));
        }
    }

    /**
     * Deserializes a {@link JsonElement} into an {@link OffsetTime} object.
     *
     * <p>If the JSON element is null or JSON null, the method returns null. If the JSON element is a
     * string, it is parsed back into an {@link OffsetTime} using the specified {@link DateTimeFormatter}.
     * The format of the string must match the pattern defined by the formatter; otherwise, an
     * {@link InvalidClassException} will be thrown.</p>
     *
     * @param serializer The serializer instance.
     * @param reference The {@link OffsetTime} reference class.
     * @param json The {@link JsonElement} to be deserialized, which may be null.
     * @return The deserialized {@link OffsetTime} object, or null if the input JSON element was null.
     * @throws InvalidClassException If the JSON element cannot be parsed into an {@link OffsetTime}.
     * @throws IllegalArgumentException If the JSON element does not match the expected format for {@link OffsetTime}.
     */
    @Override
    public @Nullable OffsetTime deserialize(@NotNull TestJson serializer, @NotNull Class<OffsetTime> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return OffsetTime.parse(json.getAsString(), getFormatter());
        }
    }
}