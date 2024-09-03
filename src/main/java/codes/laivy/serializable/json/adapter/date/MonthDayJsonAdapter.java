package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.JsonSerializable;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

final class MonthDayJsonAdapter implements JsonAdapter<MonthDay> {

    private final @NotNull DateTimeFormatter formatter;

    public MonthDayJsonAdapter() {
        this.formatter = DateTimeFormatter.ofPattern("MM/dd");
    }

    // Getters

    @Override
    public @NotNull Class<MonthDay> getReference() {
        return MonthDay.class;
    }

    // Serializers

    @Override
    public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable MonthDay instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.format(formatter));
        }
    }

    @Override
    public @Nullable MonthDay deserialize(@NotNull JsonSerializable serializer, @NotNull Class<MonthDay> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return MonthDay.parse(json.getAsString(), formatter);
        }
    }
}
