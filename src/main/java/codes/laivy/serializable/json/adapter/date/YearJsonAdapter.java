package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.Year;

final class YearJsonAdapter implements JsonAdapter<Year> {

    public YearJsonAdapter() {
    }

    // Getters

    @Override
    public @NotNull Class<Year> getReference() {
        return Year.class;
    }

    // Serializers

    @Override
    public @Nullable JsonElement serialize(@Nullable Year object) throws InvalidClassException {
        if (object == null) {
            return null;
        } else {
            return new JsonPrimitive(object.toString());
        }
    }

    @Override
    public @Nullable Year deserialize(@NotNull Class<Year> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return Year.parse(json.getAsString());
        }
    }
}
