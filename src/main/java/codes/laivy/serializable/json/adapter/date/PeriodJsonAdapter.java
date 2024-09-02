package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.Period;

final class PeriodJsonAdapter implements JsonAdapter<Period> {

    public PeriodJsonAdapter() {
    }

    // Getters

    @Override
    public @NotNull Class<Period> getReference() {
        return Period.class;
    }

    // Serializers

    @Override
    public @Nullable JsonElement serialize(@Nullable Period object) throws InvalidClassException {
        if (object == null) {
            return null;
        } else {
            return new JsonPrimitive(object.toString());
        }
    }

    @Override
    public @Nullable Period deserialize(@NotNull Class<Period> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return Period.parse(json.getAsString());
        }
    }

}
