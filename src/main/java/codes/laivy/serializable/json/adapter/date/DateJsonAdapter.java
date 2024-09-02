package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.util.Date;

final class DateJsonAdapter implements JsonAdapter<Date> {

    public DateJsonAdapter() {
    }

    // Getters

    @Override
    public @NotNull Class<Date> getReference() {
        return Date.class;
    }

    // Serializers

    @Override
    public @Nullable JsonElement serialize(@Nullable Date object) throws InvalidClassException {
        if (object == null) {
            return null;
        } else {
            return new JsonPrimitive(object.getTime());
        }
    }
    @Override
    public @Nullable Date deserialize(@NotNull Class<Date> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return new Date(json.getAsLong());
        }
    }
    
}