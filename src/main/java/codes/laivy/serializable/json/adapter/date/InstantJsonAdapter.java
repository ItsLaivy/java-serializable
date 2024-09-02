package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.time.Instant;

final class InstantJsonAdapter implements JsonAdapter<Instant> {

    public InstantJsonAdapter() {
    }

    // Getters
    
    @Override
    public @NotNull Class<Instant> getReference() {
        return Instant.class;
    }

    // Serializers

    @Override
    public @Nullable JsonElement serialize(@Nullable Instant object) throws InvalidClassException {
        if (object == null) {
            return null;
        } else {
            return new JsonPrimitive(object.toString());
        }
    }
    @Override
    public @Nullable Instant deserialize(@NotNull Class<Instant> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return Instant.parse(json.getAsString());
        }
    }
    
}