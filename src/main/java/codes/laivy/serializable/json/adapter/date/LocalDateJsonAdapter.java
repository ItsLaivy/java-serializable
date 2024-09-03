package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.TestJson;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A JSON adapter for serializing and deserializing {@link LocalDate} objects to and from JSON
 * using {@link JsonElement}. This adapter utilizes {@link DateTimeFormatter} to handle the
 * conversion between {@link LocalDate} instances and their string representations in JSON format.
 *
 * <p>The {@link LocalDateJsonAdapter} class implements the {@link JsonAdapter} interface for
 * {@link LocalDate}, enabling the conversion between {@link LocalDate} objects and JSON strings.
 * It provides the flexibility to use either the default ISO format or a custom date format specified
 * by the user.</p>
 *
 * <p>By default, this adapter uses {@link DateTimeFormatter#ISO_LOCAL_DATE}, which represents
 * dates in the ISO-8601 standard format without time or time zone information (e.g., "2024-09-02").
 * This format includes only the date, in the form of year-month-day.</p>
 *
 * <p>Alternatively, users can provide a custom {@link DateTimeFormatter} during construction. This
 * allows for custom date formats to be used, which can be tailored to specific application requirements
 * or data standards.</p>
 *
 * <p>During serialization, the {@link LocalDate} object is formatted into a string using the
 * specified {@link DateTimeFormatter}. This string is then wrapped in a {@link JsonPrimitive} for
 * inclusion in the JSON output. If the {@link LocalDate} object is null, the method returns null.</p>
 *
 * <p>During deserialization, the JSON string is parsed back into a {@link LocalDate} object using the
 * provided {@link DateTimeFormatter}. The JSON element must be a valid string representation of a date
 * in the format defined by the formatter. If the JSON element is null or JSON null, or if parsing fails,
 * the method returns null or throws an {@link InvalidClassException} respectively.</p>
 *
 * <p>Null handling: Both serialization and deserialization methods handle null values by returning
 * null when the input is null or JSON null. This ensures that the adapter gracefully handles the
 * absence of data without exceptions.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public final class LocalDateJsonAdapter implements JsonAdapter<LocalDate> {

    private final @NotNull DateTimeFormatter formatter;

    /**
     * Constructs a {@code LocalDateJsonAdapter} using the default {@link DateTimeFormatter}
     * {@link DateTimeFormatter#ISO_LOCAL_DATE}. This formatter provides a standard ISO-8601 representation
     * for {@link LocalDate}, including only the date component.
     */
    public LocalDateJsonAdapter() {
        this.formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    /**
     * Constructs a {@code LocalDateJsonAdapter} with a custom {@link DateTimeFormatter}.
     *
     * <p>The custom formatter allows for specifying a particular date format for serializing and deserializing
     * {@link LocalDate} objects. This flexibility is useful for aligning with specific date format requirements
     * or standards.</p>
     *
     * @param formatter The {@link DateTimeFormatter} to be used for formatting and parsing {@link LocalDate}.
     *                  Must not be null and should be compatible with {@link LocalDate} objects.
     */
    public LocalDateJsonAdapter(@NotNull DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    // Getters

    /**
     * Returns the class reference for {@link LocalDate}.
     *
     * @return A non-null {@link Class} object representing {@link LocalDate}. This reference is used
     *         to identify the type handled by this adapter.
     */
    @Override
    public @NotNull Class<LocalDate> getReference() {
        return LocalDate.class;
    }

    /**
     * Returns the {@link DateTimeFormatter} used by this adapter for formatting and parsing {@link LocalDate}.
     *
     * @return The non-null {@link DateTimeFormatter} instance that defines the serialization and deserialization
     *         format for {@link LocalDate}.
     */
    public @NotNull DateTimeFormatter getFormatter() {
        return formatter;
    }

    // Serializers

    /**
     * Serializes a {@link LocalDate} object into a {@link JsonElement}.
     *
     * <p>If the {@link LocalDate} object is null, the method returns null. Otherwise, the {@link LocalDate}
     * is formatted into a string using the specified {@link DateTimeFormatter}. The formatted string is then
     * wrapped in a {@link JsonPrimitive} for inclusion in the JSON output.</p>
     *
     * @param serializer The serializer instance.
     * @param instance The {@link LocalDate} to be serialized, which may be null.
     * @return A {@link JsonPrimitive} containing the serialized string representation of the {@link LocalDate},
     *         or null if the input {@link LocalDate} was null.
     * @throws InvalidClassException If the {@link LocalDate} cannot be serialized by this adapter.
     */
    @Override
    public @Nullable JsonElement serialize(@NotNull TestJson serializer, @Nullable LocalDate instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.format(getFormatter()));
        }
    }

    /**
     * Deserializes a {@link JsonElement} into a {@link LocalDate} object.
     *
     * <p>If the JSON element is null or JSON null, the method returns null. If the JSON element is a
     * string, it is parsed back into a {@link LocalDate} using the specified {@link DateTimeFormatter}.
     * The JSON string must adhere to the format defined by the formatter; otherwise, an
     * {@link InvalidClassException} will be thrown.</p>
     *
     * @param serializer The serializer instance.
     * @param json The {@link JsonElement} to be deserialized, which may be null.
     * @param reference The {@link LocalDate} reference class.
     * @return The deserialized {@link LocalDate} object, or null if the input JSON element was null.
     * @throws InvalidClassException If the JSON element cannot be parsed into a {@link LocalDate}.
     * @throws IllegalArgumentException If the JSON element does not match the expected format for {@link LocalDate}.
     */
    @Override
    public @Nullable LocalDate deserialize(@NotNull TestJson serializer, @NotNull Class<LocalDate> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return LocalDate.parse(json.getAsString(), getFormatter());
        }
    }
}
