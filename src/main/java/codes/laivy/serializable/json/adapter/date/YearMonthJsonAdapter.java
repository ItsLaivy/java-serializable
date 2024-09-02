package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.YearMonth;

final class YearMonthJsonAdapter implements JsonAdapter<YearMonth> {

    public YearMonthJsonAdapter() {
    }

    // Getters

    @Override
    public @NotNull Class<YearMonth> getReference() {
        return YearMonth.class;
    }

    // Serializers

    @Override
    public @Nullable JsonElement serialize(@Nullable YearMonth object) throws InvalidClassException {
        if (object == null) {
            return null;
        } else {
            return new JsonPrimitive(object.toString());
        }
    }

    @Override
    public @Nullable YearMonth deserialize(@NotNull Class<YearMonth> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return YearMonth.parse(json.getAsString());
        }
    }
}
