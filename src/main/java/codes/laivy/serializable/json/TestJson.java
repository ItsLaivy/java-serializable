package codes.laivy.serializable.json;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.KnownAs;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class TestJson implements Serializer<JsonElement> {

    private final @NotNull Utilities utilities = new Utilities();
    private final @NotNull JsonAdapters adapters = new JsonAdapters();

    @Override
    public @NotNull Adapters<JsonElement> getAdapters() {
        return adapters;
    }

    @Override
    public @Nullable JsonObject serialize(@Nullable Serializable object) throws InvalidClassException {
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
    public @Nullable JsonObject serialize(@Nullable Object object) throws InvalidClassException {
        if (object == null) {
            return null;
        }

        @NotNull Map<Class<?>, Set<Integer>> map = new HashMap<>();
        @NotNull JsonObject json = utilities.serializeObject(object, map);

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

    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable JsonElement object) throws InvalidClassException {
        if (Modifier.isAbstract(reference.getModifiers())) {
            throw new InvalidClassException("cannot deserialize an abstract class reference!");
        }

        return null;
    }

    @Override
    public <E> @NotNull Collection<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull JsonElement @Nullable ... array) throws InvalidClassException {
        if (Modifier.isAbstract(reference.getModifiers())) {
            throw new InvalidClassException("cannot deserialize an abstract class reference!");
        }

        return null;
    }

    // Classes

    private final class Utilities {

        private Utilities() {
        }

        private @NotNull JsonArray serializeArray(@Nullable Object @NotNull [] array, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
            @NotNull JsonArray json = new JsonArray();

            for (@Nullable Object object : array) {
                if (object == null) {
                    json.add(JsonNull.INSTANCE);
                } else if (object.getClass().isArray()) {
                    json.add(serializeArray(toObjectArray(array), map));
                } else {
                    System.out.println("Object: '" + serializeObject(object, map) + "'");
                    json.add(serializeObject(object, map));
                }
            }

            return json;
        }
        private @NotNull JsonObject serializeObject(@NotNull Object object, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
            if (object.getClass().isArray()) {
                throw new InvalidClassException("cannot deserialize an array object using this method");
            }

            // Start serialization
            @NotNull JsonObject json = new JsonObject();

            // Start serializes the fields
            @NotNull Class<?> type = object.getClass();

            // Start looking fields into class and superclasses
            while (type != Object.class) {
                for (@NotNull Field field : getFields(type)) {
                    serializeField(json, object, field, map);
                }

                // Finish with the superclass
                type = type.getSuperclass();
            }

            return json;
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        private void serializeField(@NotNull JsonObject object, @NotNull Object instance, @NotNull Field field, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
            try {
                // Check if not transient or static
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    return;
                }

                // Check accessibility
                boolean accessible = field.isAccessible();
                if (!accessible) field.setAccessible(true);

                // Get value and prevent recursive serialization
                @Nullable Object value = field.get(instance);
                if (value != null) {
                    int hash = value.hashCode();

                    @NotNull Set<Integer> set = map.computeIfAbsent(value.getClass(), k -> new HashSet<>());
                    if (set.contains(hash)) return;

                    set.add(hash);
                }

                // Get field name (or @KnownAs name)
                @NotNull String name = field.getName();

                if (field.isAnnotationPresent(KnownAs.class)) {
                    name = field.getAnnotation(KnownAs.class).name();
                }

                // Serialize field value and add it to JSON
                @Nullable Adapter adapter = value == null ? null : getAdapters().get(value.getClass()).orElse(null);
                @Nullable JsonElement element;

                if (adapter != null) {
                    element = (JsonElement) adapter.serialize(value);
                } else if (value == null) {
                    element = JsonNull.INSTANCE;
                } else if (value instanceof Enum<?>) {
                    element = serialize((Enum<?>) value);
                } else if (value instanceof Enum<?>[]) {
                    element = serialize((Enum<?>[]) value);
                } else if (value instanceof Boolean) {
                    element = serialize((Boolean) value);
                } else if (value instanceof Boolean[]) {
                    element = serialize((Boolean[]) value);
                } else if (value instanceof Byte) {
                    element = serialize((Byte) value);
                } else if (value instanceof Byte[]) {
                    element = serialize((Byte[]) value);
                } else if (value instanceof Short) {
                    element = serialize((Short) value);
                } else if (value instanceof Float) {
                    element = serialize((Float) value);
                } else if (value instanceof Double) {
                    element = serialize((Double) value);
                } else if (value instanceof Number) {
                    element = serialize((Number) value);
                } else if (value instanceof Number[]) {
                    element = serialize((Number[]) value);
                } else if (value instanceof Character) {
                    element = serialize((Character) value);
                } else if (value instanceof Character[]) {
                    element = serialize((Character[]) value);
                } else if (value instanceof char[]) {
                    element = serialize((char[]) value);
                } else if (value instanceof boolean[]) {
                    element = serialize((boolean[]) value);
                } else if (value instanceof byte[]) {
                    element = serialize((byte[]) value);
                } else if (value instanceof int[]) {
                    element = serialize((int[]) value);
                } else if (value instanceof short[]) {
                    element = serialize((short[]) value);
                } else if (value instanceof long[]) {
                    element = serialize((long[]) value);
                } else if (value instanceof float[]) {
                    element = serialize((float[]) value);
                } else if (value instanceof double[]) {
                    element = serialize((double[]) value);
                } else if (value instanceof String) {
                    element = serialize((String) value);
                } else if (value instanceof String[]) {
                    element = serialize((String[]) value);
                } else if (value instanceof Object[]) {
                    element = serialize((Object[]) value);
                } else {
                    element = serializeObject(object, map);
                }

                object.add(name, element);

                // Make it inaccessible again
                field.setAccessible(accessible);
            } catch (@NotNull IllegalAccessException e) {
                throw new RuntimeException("cannot access field '" + field.getName() + "' to proceed serialization", e);
            }
        }

        private @NotNull Field[] getFields(@NotNull Class<?> type) {
            return Stream.of(type.getFields(), type.getDeclaredFields()).flatMap(Arrays::stream).distinct().toArray(Field[]::new);
        }

        private @Nullable Object @NotNull [] toObjectArray(@NotNull Object array) {
            if (!array.getClass().isArray()) {
                throw new IllegalArgumentException("the provided object is not an array");
            }

            int length = Array.getLength(array);
            @Nullable Object @NotNull [] objectArray = new Object[length];

            for (int i = 0; i < length; i++) {
                objectArray[i] = Array.get(array, i);
            }

            return objectArray;
        }

    }
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
            return map.containsKey(reference);
        }

        @Override
        public boolean contains(@NotNull Adapter<JsonElement, ?> adapter) {
            return map.containsValue(adapter);
        }

        @Override
        public @NotNull <E> Optional<Adapter<JsonElement, E>> get(@NotNull Class<E> reference) {
            //noinspection unchecked
            return Optional.ofNullable((Adapter<JsonElement, E>) map.getOrDefault(reference, null));
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
