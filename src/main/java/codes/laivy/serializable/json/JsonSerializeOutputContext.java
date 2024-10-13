package codes.laivy.serializable.json;

import codes.laivy.serializable.context.SerializeOutputContext;
import codes.laivy.serializable.exception.NullConcreteClassException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static codes.laivy.serializable.utilities.Classes.isConcrete;

final class JsonSerializeOutputContext implements SerializeOutputContext {

    private final @NotNull JsonSerializer serializer;
    private final @NotNull Class<?> reference;

    final @NotNull List<@Nullable Object> objects = new LinkedList<>();
    final @NotNull Map<String, @Nullable Object> fields = new LinkedHashMap<>();

    public JsonSerializeOutputContext(@NotNull JsonSerializer serializer, @NotNull Class<?> reference) {
        this.reference = reference;
        this.serializer = serializer;

        // Check modifiers
        if (!isConcrete(reference)) {
            throw new NullConcreteClassException("the reference '" + reference + "' isn't concrete.");
        }
    }

    // Getters

    @Override
    public @NotNull Class<?> getReference() {
        return reference;
    }

    public @NotNull List<Object> getObjects() {
        return objects;
    }
    public @NotNull Map<String, Object> getFields() {
        return fields;
    }

    // Modules

    @Override
    public void write(@NotNull String name, @Nullable Object value) {
        fields.put(name, value);
    }
    @Override
    public void write(@NotNull String name, @Nullable Serializable serializable) {
        fields.put(name, serializable);
    }

    @Override
    public void write(boolean bool) {
        objects.add(bool);
    }

    @Override
    public void write(byte b) {
        objects.add(b);
    }

    @Override
    public void write(short s) {
        objects.add(s);
    }

    @Override
    public void write(char c) {
        objects.add(c);
    }

    @Override
    public void write(int i) {
        objects.add(i);
    }

    @Override
    public void write(long l) {
        objects.add(l);
    }

    @Override
    public void write(float f) {
        objects.add(f);
    }

    @Override
    public void write(double d) {
        objects.add(d);
    }

    @Override
    public void write(@Nullable String string) {
        objects.add(string);
    }

    @Override
    public void write(@Nullable Object object) {
        objects.add(object);
    }

    @Override
    public @NotNull JsonSerializer getSerializer() {
        return serializer;
    }

    public @NotNull JsonElement serialize() {
        @NotNull JsonElement json;
        
        if (getObjects().isEmpty()) {
            json = new JsonObject();

            for (@NotNull Map.Entry<String, Object> entry : getFields().entrySet()) {
                @NotNull String name = entry.getKey();
                @Nullable Object value = entry.getValue();

                // Serialize object
                @Nullable JsonElement serialized;

                if (value != null) {
                    serialized = new SerializingProcess(getSerializer(), value.getClass(), null).serialize(value);
                } else {
                    serialized = JsonNull.INSTANCE;
                }

                // Finish
                ((JsonObject) json).add(name, serialized);
            }
        } else if (getObjects().size() == 1 && getFields().isEmpty()) {
            @Nullable Object value = objects.get(0);
            return value != null ? new SerializingProcess(getSerializer(), value.getClass(), null).serialize(value) : JsonNull.INSTANCE;
        } else {
            json = new JsonArray();

            // Objects
            for (@Nullable Object value : objects) {
                if (value == null) {
                    ((JsonArray) json).add(JsonNull.INSTANCE);
                } else {
                    @Nullable JsonElement element = new SerializingProcess(getSerializer(), value.getClass(), null).serialize(value);
                    ((JsonArray) json).add(element);
                }
            }

            // Fields
            @NotNull JsonObject fields = new JsonObject();

            for (@NotNull Map.Entry<String, Object> entry : getFields().entrySet()) {
                @NotNull String name = "!" + entry.getKey();
                @Nullable Object value = entry.getValue();

                if (value != null) {
                    fields.add(name, new SerializingProcess(getSerializer(), value.getClass(), null).serialize(value));
                } else {
                    fields.add(name, JsonNull.INSTANCE);
                }
            }

            if (!fields.isEmpty()) {
                ((JsonArray) json).add(fields);
            }
        }
        
        return json;
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return "JsonSerializeOutputContext{" +
                "reference=" + reference +
                ", objects=" + objects +
                ", fields=" + fields +
                '}';
    }

}
