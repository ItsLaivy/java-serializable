package codes.laivy.serializable.json.provided;

import codes.laivy.serializable.json.JsonAdapter;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;

public final class GsonJsonAdapter implements JsonAdapter<JsonElement> {

    @Override
    public @NotNull Class<JsonElement> getReference() {
        return JsonElement.class;
    }

    @Override
    public @Nullable JsonElement serialize(@Nullable JsonElement object) throws InvalidClassException {
        return object;
    }
    @Override
    public @Nullable JsonElement deserialize(@Nullable JsonElement object) throws InvalidClassException {
        return object;
    }

}
