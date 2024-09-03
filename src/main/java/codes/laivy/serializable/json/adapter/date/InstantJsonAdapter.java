package codes.laivy.serializable.json.adapter.date;

import codes.laivy.serializable.json.JsonSerializable;
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
    public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable Instant instance) throws InvalidClassException {
        if (instance == null) {
            return null;
        } else {
            return new JsonPrimitive(instance.toString());
        }
    }
    @Override
    public @Nullable Instant deserialize(@NotNull JsonSerializable serializer, @NotNull Class<Instant> reference, @Nullable JsonElement json) throws InvalidClassException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else {
            return Instant.parse(json.getAsString());
        }
    }
    
}