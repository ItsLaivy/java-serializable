package codes.laivy.serializable.json.provided;

import codes.laivy.serializable.json.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateJsonAdapter {

    // Object

    private DateJsonAdapter() {
    }

    // Classes

    public static final class OffsetDateTime implements JsonAdapter<java.time.OffsetDateTime> {
        @Override
        public @NotNull Class<java.time.OffsetDateTime> getReference() {
            return java.time.OffsetDateTime.class;
        }

        @Override
        public @Nullable JsonPrimitive serialize(@Nullable java.time.OffsetDateTime object) throws InvalidClassException {
            if (object == null) return null;
            return new JsonPrimitive(object.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        @Override
        public @Nullable java.time.OffsetDateTime deserialize(@Nullable JsonElement element) throws InvalidClassException {
            if (element == null || element.isJsonNull()) {
                return null;
            } else if (element.isJsonPrimitive()) try {
                return java.time.OffsetDateTime.parse(element.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (@NotNull DateTimeParseException e) {
                throw new IllegalArgumentException("cannot parse '" + element.getAsString() + "' into a valid OffsetDateTime ISO offset date time");
            } else {
                throw new IllegalArgumentException("the OffsetDateTime element must be a string primitive json '" + element + "'");
            }
        }
    }

}
