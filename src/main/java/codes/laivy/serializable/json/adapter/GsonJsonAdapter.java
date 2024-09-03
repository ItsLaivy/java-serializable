package codes.laivy.serializable.json.adapter;

import codes.laivy.serializable.json.JsonSerializable;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;

final class GsonJsonAdapter implements JsonAdapter<JsonElement> {

    public GsonJsonAdapter() {
    }

    @Override
    public @NotNull Class<JsonElement> getReference() {
        return JsonElement.class;
    }

    @Override
    public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable JsonElement instance) throws InvalidClassException {
        return instance;
    }
    @Override
    public @Nullable JsonElement deserialize(@NotNull JsonSerializable serializer, @NotNull Class<JsonElement> reference, @Nullable JsonElement object) throws InvalidClassException {
        return object;
    }

}
