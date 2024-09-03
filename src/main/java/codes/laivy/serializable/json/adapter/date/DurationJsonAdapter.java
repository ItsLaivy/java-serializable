package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.JsonSerializable;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * This adapter transforms a {@link Duration} object into a readable JSON format.
 * The adapter includes a single variable called {@code timeUnit}. When this variable is not null,
 * the {@link Duration} is serialized and deserialized according to the temporal unit represented by
 * the {@code timeUnit}.
 *
 * <p>With a {@code timeUnit}, the serialization might look like this (for {@link TimeUnit#SECONDS}):
 * {@code 60} - Representing one minute.</p>
 * <p>Without a {@code timeUnit}, the serialization might look like this:
 * {@code {"nano":0,"seconds":60}} - Representing one minute.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public final class DurationJsonAdapter implements JsonAdapter<Duration> {

    private final @Nullable TimeUnit timeUnit;

    /**
     * Constructs a {@code DurationJsonAdapter} with no specific {@link TimeUnit}.
     * The {@link Duration} will be serialized and deserialized as a JSON object with
     * both {@code nano} and {@code seconds} properties.
     */
    public DurationJsonAdapter() {
        this.timeUnit = null;
    }

    /**
     * Constructs a {@code DurationJsonAdapter} with a specific {@link TimeUnit}.
     * The {@link Duration} will be serialized and deserialized according to the
     * temporal unit represented by this {@code timeUnit}.
     *
     * @param timeUnit The {@link TimeUnit} to be used for serialization and deserialization,
     *                 or null to use the default JSON object representation.
     */
    public DurationJsonAdapter(@Nullable TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    // Getters

    /**
     * Returns the class reference of the type {@link Duration} that this adapter handles.
     *
     * @return A non-null {@link Class} object representing {@link Duration}.
     */
    @Override
    public @NotNull Class<Duration> getReference() {
        return Duration.class;
    }

    /**
     * Returns the {@link TimeUnit} used by this adapter for serialization and deserialization.
     *
     * @return The {@link TimeUnit}, or null if the adapter uses the default JSON object representation.
     */
    public @Nullable TimeUnit getTimeUnit() {
        return timeUnit;
    }

    // Serializers

    /**
     * Serializes a {@link Duration} object into a {@link JsonElement}.
     *
     * <p>If a {@link TimeUnit} is specified, the {@link Duration} is serialized as a
     * single numeric value representing the duration in terms of the {@code timeUnit}.
     * If no {@code timeUnit} is specified, the {@link Duration} is serialized as a JSON
     * object with {@code nano} and {@code seconds} properties.</p>
     *
     * @param serializer The serializer instance.
     * @param instance The {@link Duration} to be serialized, which may be null.
     * @return A {@link JsonElement} representing the serialized form of the {@link Duration}, or null if the input was null.
     * @throws InvalidClassException If the {@link Duration} cannot be serialized by this adapter.
     */
    @Override
    public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable Duration instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else if (getTimeUnit() != null) {
            long durationInNanos = instance.toNanos();
            long unitInNanos = TimeUnit.NANOSECONDS.convert(1, timeUnit);

            @NotNull BigDecimal result = BigDecimal.valueOf(durationInNanos)
                    .divide(BigDecimal.valueOf(unitInNanos), 15, RoundingMode.HALF_UP)
                    .stripTrailingZeros();

            return new JsonPrimitive(result);
        } else {
            @NotNull JsonObject json = new JsonObject();
            json.addProperty("nano", instance.getNano());
            json.addProperty("seconds", instance.getSeconds());

            return json;
        }
    }

    /**
     * Deserializes a {@link JsonElement} back into a {@link Duration}.
     *
     * <p>If a {@link TimeUnit} is specified, the {@link JsonElement} is expected to be a
     * numeric value that will be converted back into a {@link Duration} based on the {@code timeUnit}.
     * If no {@code timeUnit} is specified, the {@link JsonElement} is expected to be a JSON object
     * with {@code nano} and {@code seconds} properties.</p>
     *
     * @param serializer The serializer instance.
     * @param reference The duration class reference.
     * @param json The {@link JsonElement} to be deserialized, which may be null.
     *
     * @return The deserialized {@link Duration}, or null if the input was null.
     * @throws InvalidClassException If the {@link JsonElement} cannot be deserialized into a {@link Duration}.
     * @throws IllegalArgumentException If the input JSON is not in the expected format for the given {@code timeUnit}.
     */
    @Override
    public @Nullable Duration deserialize(@NotNull JsonSerializable serializer, @NotNull Class<Duration> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else if (getTimeUnit() != null) {
            if (!json.isJsonPrimitive()) {
                throw new IllegalArgumentException("a json primitive was expected while parsing '" + json + "' into a valid '" + getReference() + "' object");
            }

            double value = json.getAsDouble();
            long wholePart = (long) value;  // Parte inteira do valor
            @NotNull BigDecimal fractionalPart = BigDecimal.valueOf(value).subtract(BigDecimal.valueOf(wholePart));  // Parte fracionária do valor

            long nanosFromWholePart = TimeUnit.NANOSECONDS.convert(wholePart, timeUnit);

            @NotNull BigDecimal nanosPerUnit = BigDecimal.valueOf(TimeUnit.NANOSECONDS.convert(1, timeUnit));
            @NotNull BigDecimal nanosFromFractionalPart = fractionalPart.multiply(nanosPerUnit);

            long totalNanos = nanosFromWholePart + nanosFromFractionalPart.setScale(0, RoundingMode.HALF_UP).longValue();

            return Duration.ofNanos(totalNanos);
        } else if (json.isJsonObject()) {
            @NotNull JsonObject object = json.getAsJsonObject();
            long nano = object.get("nano").getAsLong();
            long seconds = object.get("seconds").getAsLong();

            return Duration.ofSeconds(seconds, nano);
        } else {
            throw new IllegalArgumentException("a json object was expected while parsing '" + json + "' into a valid '" + getReference() + "' object");
        }
    }

}
