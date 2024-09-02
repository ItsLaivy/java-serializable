package codes.laivy.serializable.json;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestJson implements Serializer<JsonElement> {

    private final @NotNull JsonAdapters adapters = new JsonAdapters();

    @Override
    public @NotNull Adapters<JsonElement> getAdapters() {
        return adapters;
    }

    @Override
    public @Nullable JsonElement serialize(@Nullable Serializable object) throws InvalidClassException {
        if (object == null) return null;
        return serialize((Object) object);
    }

    @Override
    public @Nullable JsonArray serialize(@Nullable Serializable @NotNull ... array) throws InvalidClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Serializable object : array) {
            json.add(serialize(object));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws InvalidClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Serializable object : iterable) {
            json.add(serialize(object));
        }

        return json;
    }

    @Override
    public @Nullable JsonPrimitive serialize(@Nullable Enum<?> e) {
        return e != null ? new JsonPrimitive(e.name()) : null;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Enum<?> @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Enum<?> e : array) {
            json.add(e != null ? e.name() : null);
        }

        return json;
    }

    @Override
    public @Nullable JsonPrimitive serialize(@Nullable Boolean b) {
        return b != null ? new JsonPrimitive(b) : null;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Boolean e : array) {
            json.add(e != null ? new JsonPrimitive(e) : null);
        }

        return json;
    }

    @Override
    public @Nullable JsonPrimitive serialize(@Nullable Number number) {
        return number != null ? new JsonPrimitive(number) : null;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Number @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Number e : array) {
            json.add(e != null ? new JsonPrimitive(e) : null);
        }

        return json;
    }

    @Override
    public @Nullable JsonPrimitive serialize(@Nullable Character c) {
        return c != null ? new JsonPrimitive(c) : null;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Character @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Character e : array) {
            json.add(e != null ? new JsonPrimitive(e) : null);
        }

        return json;
    }

    @Override
    public @Nullable JsonPrimitive serialize(@Nullable Byte b) {
        return b != null ? new JsonPrimitive(b) : null;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Byte e : array) {
            json.add(e != null ? new JsonPrimitive(e) : null);
        }

        return json;
    }

    @Override
    public @Nullable JsonPrimitive serialize(@Nullable String string) {
        return string != null ? new JsonPrimitive(string) : null;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable String @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable String e : array) {
            json.add(e != null ? new JsonPrimitive(e) : null);
        }

        return json;
    }

    @Override
    public @NotNull JsonPrimitive serialize(boolean b) {
        return new JsonPrimitive(b);
    }

    @Override
    public @NotNull JsonPrimitive serialize(char c) {
        return new JsonPrimitive(c);
    }

    @Override
    public @NotNull JsonPrimitive serialize(byte b) {
        return new JsonPrimitive(b);
    }

    @Override
    public @NotNull JsonPrimitive serialize(short s) {
        return new JsonPrimitive(s);
    }

    @Override
    public @NotNull JsonPrimitive serialize(int i) {
        return new JsonPrimitive(i);
    }

    @Override
    public @NotNull JsonPrimitive serialize(long l) {
        return new JsonPrimitive(l);
    }

    @Override
    public @NotNull JsonPrimitive serialize(float f) {
        return new JsonPrimitive(f);
    }

    @Override
    public @NotNull JsonPrimitive serialize(double d) {
        return new JsonPrimitive(d);
    }

    @Override
    public @NotNull JsonArray serialize(boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (boolean e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(char @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (char e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (byte e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (short e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(int @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (int e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (long e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (float e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @NotNull JsonArray serialize(double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (double e : array) json.add(serialize(e));
        return json;
    }

    @Override
    public @Nullable JsonElement serialize(@Nullable Object object) throws InvalidClassException {
        if (object == null) {
            return null;
        }

        @NotNull Map<Class<?>, Set<Integer>> map = new HashMap<>();
        @NotNull JsonElement json = JsonUtilities.serializeObject(this, object, map);

        // Finish
        return json;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Object @NotNull ... array) throws InvalidClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Object object : array) {
            json.add(serialize(object));
        }

        return json;
    }

    // Deserializer

    @SuppressWarnings({"unchecked", "rawtypes"})
    public @Nullable Object deserializeUnsafe(@NotNull Class<?> reference, @Nullable JsonElement element) throws InvalidClassException {
        @Nullable Adapter adapter = getAdapters().get(reference).orElse(null);

        if (adapter != null) {
            return adapter.deserialize(element);
        } else if (element == null || element.isJsonNull()) {
            return null;
        } else if (element.isJsonObject()) {
            @NotNull JsonObject jsonObject = element.getAsJsonObject();
            @NotNull Object instance = Allocator.allocate(reference);

            for (@NotNull String key : jsonObject.keySet()) {
                @NotNull JsonElement value = jsonObject.get(key);

                @Nullable Field field = JsonUtilities.getFieldByName(instance, key);

                if (field == null) {
                    throw new InvalidClassException("there's no field with name '" + key + "'");
                }

                if (value.isJsonNull()) {
                    Allocator.setFieldValue(field, instance, null);
                } else if (field.getType().isArray()) {
                    Allocator.setFieldValue(field, instance, deserializeUnsafe(field.getType().getComponentType(), value.getAsJsonArray()));
                } else {
                    @Nullable Object object;

                    if (field.getType() == Class.class) try {
                        object = Class.forName(value.getAsString());
                    } catch (@NotNull ClassNotFoundException e) {
                        throw new InvalidClassException("there's no class '" + value.getAsString() + "' to deserialize at runtime");
                    } else {
                        object = deserializeUnsafe(field.getType(), value);
                    }

                    Allocator.setFieldValue(field, instance, object);
                }
            }

            return instance;
        } else if (element.isJsonPrimitive()) {
            if (reference == String.class) {
                return element.getAsString();
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException("cannot deserialize '" + element + "' into a valid '" + reference + "' object");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable JsonElement element) throws InvalidClassException {
        if (Modifier.isAbstract(reference.getModifiers())) {
            throw new InvalidClassException("cannot deserialize an abstract class reference!");
        }

        // Adapter
        @Nullable Adapter adapter = getAdapters().get(reference).orElse(null);

        if (adapter != null) {
            return (E) adapter.deserialize(element);
        }

        if (JsonUtilities.usesJavaSerialization(reference)) {
            if (element == null || element.isJsonNull()) {
                return null;
            } else if (!element.isJsonArray()) {
                throw new IllegalArgumentException("cannot deserialize '" + element + "' into a valid '" + reference + "' object");
            }

            @NotNull JsonArray array = element.getAsJsonArray();
            byte[] bytes = new byte[array.size()];

            int row = 0;
            for (@NotNull JsonElement e : array) {
                bytes[row] = e.getAsByte();
                row++;
            }

            try {
                @NotNull ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
                //noinspection unchecked
                return (E) stream.readObject();
            } catch (@NotNull IOException | @NotNull ClassNotFoundException e) {
                throw new RuntimeException("an unknown error occurred trying to deserialize reference '" + reference + "' with json data '" + element + "'");
            }
        } else {
            if (element == null || element.isJsonNull()) {
                return null;
            }

            //noinspection unchecked
            return (E) deserializeUnsafe(reference, element);
        }
    }

    @Override
    public <E> @NotNull Collection<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable JsonElement @NotNull ... array) throws InvalidClassException {
        if (Modifier.isAbstract(reference.getModifiers())) {
            throw new InvalidClassException("cannot deserialize an abstract class reference!");
        }

        @NotNull List<E> list = new ArrayList<>();

        for (@Nullable JsonElement object : array) {
            list.add(object != null ? deserialize(reference, object) : null);
        }

        return list;
    }

    // Classes

    private static final class JsonAdapters implements Adapters<JsonElement> {

        private final @NotNull Map<Class<?>, Adapter<JsonElement, ?>> map = new HashMap<>();

        private JsonAdapters() {
        }

        @Override
        public <E> void put(@NotNull Adapter<JsonElement, E> adapter) {
            map.put(adapter.getReference(), adapter);
        }

        @Override
        public <E> boolean add(@NotNull Adapter<JsonElement, E> adapter) {
            if (map.containsKey(adapter.getReference())) {
                return false;
            }

            map.put(adapter.getReference(), adapter);
            return true;
        }

        @Override
        public <E> boolean remove(@NotNull Class<E> reference) {
            if (map.containsKey(reference)) {
                map.remove(reference);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(@NotNull Class<?> reference) {
            @Nullable Adapter<JsonElement, ?> adapter = map.getOrDefault(reference, null);
            if (adapter != null) return true;

            // Check assignable
            for (@NotNull Class<?> r : map.keySet()) {
                if (r.isAssignableFrom(reference)) {
                    return map.get(r) != null;
                }
            }

            return false;
        }

        @Override
        public boolean contains(@NotNull Adapter<JsonElement, ?> adapter) {
            return map.containsValue(adapter);
        }

        @Override
        public @NotNull <E> Optional<Adapter<JsonElement, E>> get(@NotNull Class<E> reference) {
            //noinspection unchecked
            @Nullable Adapter<JsonElement, E> adapter = (Adapter<JsonElement, E>) map.getOrDefault(reference, null);
            if (adapter != null) return Optional.of(adapter);

            // Check assignable
            for (@NotNull Class<?> r : map.keySet()) {
                if (r.isAssignableFrom(reference)) {
                    //noinspection unchecked
                    return Optional.ofNullable((Adapter<JsonElement, E>) map.get(r));
                }
            }

            return Optional.empty();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public @NotNull Iterator<Adapter<JsonElement, ?>> iterator() {
            return map.values().iterator();
        }
    }

}
