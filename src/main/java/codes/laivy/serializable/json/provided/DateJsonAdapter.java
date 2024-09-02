package codes.laivy.serializable.json.provided;

import codes.laivy.serializable.json.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.OffsetDateTime;

public final class DateJsonAdapter implements JsonAdapter<OffsetDateTime> {
    @Override
    public @NotNull Class<OffsetDateTime> getReference() {
        return OffsetDateTime.class;
    }

    @Override
    public @Nullable JsonElement serialize(@Nullable OffsetDateTime object) throws InvalidClassException {
        if (object == null) return null;
        @NotNull JsonObject json = new JsonObject();

        json.addProperty("test", "a");

        return json;
    }

    @Override
    public @Nullable OffsetDateTime deserialize(@Nullable JsonElement object) throws InvalidClassException {
        return OffsetDateTime.now();
    }
}
