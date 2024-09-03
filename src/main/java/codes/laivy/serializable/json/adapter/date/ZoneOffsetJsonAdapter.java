package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.TestJson;
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
    public @Nullable JsonElement serialize(@NotNull TestJson serializer, @Nullable ZoneOffset instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.getId());
        }
    }
    @Override
    public @Nullable ZoneOffset deserialize(@NotNull TestJson serializer, @NotNull Class<ZoneOffset> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return ZoneOffset.of(json.getAsString());
        }
    }

}
