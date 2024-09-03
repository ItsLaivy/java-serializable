package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.JsonSerializable;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.ZoneId;

final class ZoneIdJsonAdapter implements JsonAdapter<ZoneId> {

    public ZoneIdJsonAdapter() {
    }

    @Override
    public @NotNull Class<ZoneId> getReference() {
        return ZoneId.class;
    }

    @Override
    public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable ZoneId instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.toString());
        }
    }
    @Override
    public @Nullable ZoneId deserialize(@NotNull JsonSerializable serializer, @NotNull Class<ZoneId> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return ZoneId.of(json.getAsString());
        }
    }

}
