package codes.laivy.serializable.json.adapter;

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
    public @Nullable JsonElement serialize(@Nullable JsonElement object) throws InvalidClassException {
        return object;
    }
    @Override
    public @Nullable JsonElement deserialize(@NotNull Class<JsonElement> reference, @Nullable JsonElement object) throws InvalidClassException {
        return object;
    }

}
