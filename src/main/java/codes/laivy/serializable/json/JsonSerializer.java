package codes.laivy.serializable.json;

import codes.laivy.serializable.TypeSerializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.adapter.provided.CharacterArrayAdapter;
import codes.laivy.serializable.adapter.provided.TemporalAdapter;
import codes.laivy.serializable.adapter.provided.UUIDAdapter;
import codes.laivy.serializable.exception.MalformedClassException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.Serializable;
import java.util.*;

// todo: gson adapter
public class JsonSerializer implements TypeSerializer<JsonElement> {

    // Object

    final @NotNull Map<Class<?>, Adapter> adapterMap = new HashMap<>();

    public JsonSerializer() {
        @NotNull Adapter[] adapters = new Adapter[]{
                new TemporalAdapter(),
                new CharacterArrayAdapter(),
                new UUIDAdapter()
        };

        for (@NotNull Adapter adapter : adapters) {
            for (@NotNull Class<?> reference : adapter.getReferences()) {
                adapterMap.put(reference, adapter);
            }
        }
    }

    // Getters

    @Override
    public @NotNull Collection<Adapter> getAdapters() {
        return adapterMap.values();
    }

    // Serializers and Deserializers

    @Override
    public @NotNull JsonElement serialize(@Nullable Serializable object) throws MalformedClassException {
        return this.serialize((Object) object);
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Object object) throws MalformedClassException {
        if (object == null) {
            return JsonNull.INSTANCE;
        }

        @NotNull SerializingProcess process = new SerializingProcess(this, object.getClass());
        return process.serialize(object);
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Object @NotNull ... array) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Object object : array) {
            json.add(serialize(object));
        }

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Serializable serializable : array) {
            json.add(serialize(serializable));
        }

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Serializable serializable : iterable) {
            json.add(serialize(serializable));
        }

        return json;
    }

    @Override
    public @NotNull JsonElement serialize(@Nullable Enum<?> e) {
        return e != null ? new JsonPrimitive(e.name()) : JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Enum<?> @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Enum<?> e : array) {
            if (e != null) json.add(new JsonPrimitive(e.name()));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Boolean b) {
        return b != null ? new JsonPrimitive(b): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Boolean b : array) {
            if (b != null) json.add(new JsonPrimitive(b));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Short s) {
        return s != null ? new JsonPrimitive(s): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Short s : array) {
            if (s != null) json.add(new JsonPrimitive(s));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Integer i) {
        return i != null ? new JsonPrimitive(i): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Integer @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Integer i : array) {
            if (i != null) json.add(new JsonPrimitive(i));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Long l) {
        return l != null ? new JsonPrimitive(l): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Long l : array) {
            if (l != null) json.add(new JsonPrimitive(l));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Float f) {
        return f != null ? new JsonPrimitive(f): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Float f : array) {
            if (f != null) json.add(new JsonPrimitive(f));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Double d) {
        return d != null ? new JsonPrimitive(d): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Double d : array) {
            if (d != null) json.add(new JsonPrimitive(d));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Character c) {
        return c != null ? new JsonPrimitive(c): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Character @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Character c : array) {
            if (c != null) json.add(new JsonPrimitive(c));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Byte b) {
        return b != null ? new JsonPrimitive(b): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Byte b : array) {
            if (b != null) json.add(new JsonPrimitive(b));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable String string) {
        return string != null ? new JsonPrimitive(string): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable String @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable String string : array) {
            if (string != null) json.add(new JsonPrimitive(string));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }

    @Override
    public @NotNull JsonElement serialize(boolean b) {
        return new JsonPrimitive(b);
    }
    @Override
    public @NotNull JsonElement serialize(char c) {
        return new JsonPrimitive(c);
    }
    @Override
    public @NotNull JsonElement serialize(byte b) {
        return new JsonPrimitive(b);
    }
    @Override
    public @NotNull JsonElement serialize(short s) {
        return new JsonPrimitive(s);
    }
    @Override
    public @NotNull JsonElement serialize(int i) {
        return new JsonPrimitive(i);
    }
    @Override
    public @NotNull JsonElement serialize(long l) {
        return new JsonPrimitive(l);
    }
    @Override
    public @NotNull JsonElement serialize(float f) {
        return new JsonPrimitive(f);
    }
    @Override
    public @NotNull JsonElement serialize(double d) {
        return new JsonPrimitive(d);
    }

    @Override
    public @NotNull JsonArray serialize(boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (boolean b : array) json.add(new JsonPrimitive(b));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(char @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (char c : array) json.add(new JsonPrimitive(c));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (byte b : array) json.add(new JsonPrimitive(b));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (short s : array) json.add(new JsonPrimitive(s));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(int @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (int i : array) json.add(new JsonPrimitive(i));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (long l : array) json.add(new JsonPrimitive(l));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (float f : array) json.add(new JsonPrimitive(f));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (double d : array) json.add(new JsonPrimitive(d));

        return json;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable JsonElement object) throws MalformedClassException {
        if (object == null) {
            return null;
        }

        @NotNull SerializingProcess process = new SerializingProcess(this, reference);
        return (E) process.deserialize(object);
    }
    @Override
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable JsonElement @NotNull ... array) throws MalformedClassException {
        @NotNull List<E> list = new LinkedList<>();

        for (@Nullable JsonElement element : array) {
            list.add(deserialize(reference, element));
        }

        return list;
    }
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull JsonArray array) throws MalformedClassException {
        @NotNull List<E> list = new LinkedList<>();

        for (@Nullable JsonElement element : array) {
            list.add(deserialize(reference, element));
        }

        return list;
    }

    // Utilities

    @NotNull Object usingAdapter(@NotNull Class<?> reference, @NotNull JsonElement element) {
        @NotNull Object object;

        try {
            @NotNull Adapter adapter = adapterMap.get(reference);
            @NotNull JsonSerializeInputContext context = new JsonSerializeInputContext(this, reference, element);
            object = adapter.deserialize(context);
        } catch (@NotNull EOFException e) {
            throw new RuntimeException("cannot proceed adapter deserialization '" + reference + "': " + element, e);
        }

        if (!reference.isAssignableFrom(object.getClass())) {
            throw new IllegalStateException("the adapter returned '" + object.getClass() + "' that isn't assignable from '" + reference + "'");
        }

        return object;
    }

}
