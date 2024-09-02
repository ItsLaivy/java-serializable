package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.ZoneOffset;

final class ZoneOffsetJsonAdapter implements JsonAdapter<ZoneOffset> {

    public ZoneOffsetJsonAdapter() {
    }

    @Override
    public @NotNull Class<ZoneOffset> getReference() {
        return ZoneOffset.class;
    }

    @Override
    public @Nullable JsonElement serialize(@Nullable ZoneOffset object) throws InvalidClassException {
        if (object == null) {
            return null;
        } else {
            return new JsonPrimitive(object.getId());
        }
    }
    @Override
    public @Nullable ZoneOffset deserialize(@NotNull Class<ZoneOffset> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return ZoneOffset.of(json.getAsString());
        }
    }

}
