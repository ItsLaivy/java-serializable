package codes.laivy.serializable.json.provided;

import codes.laivy.serializable.json.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public final class UUIDJsonAdapter implements JsonAdapter<UUID> {

    @Override
    public @NotNull Class<UUID> getReference() {
        return UUID.class;
    }

    @Override
    public @Nullable JsonElement serialize(@Nullable UUID object) throws InvalidClassException {
        if (object == null) return null;
        else return new JsonPrimitive(object.toString());
    }
    @Override
    public @Nullable UUID deserialize(@Nullable JsonElement element) throws InvalidClassException {
        if (element == null || element.isJsonNull()) {
            return null;
        } else if (element.isJsonPrimitive()) try {
            return UUID.fromString(element.getAsString());
        } catch (@NotNull DateTimeParseException e) {
            throw new IllegalArgumentException("cannot parse '" + element.getAsString() + "' into a valid UUID");
        } else {
            throw new IllegalArgumentException("the UUID element must be a string primitive json '" + element + "'");
        }
    }

}
