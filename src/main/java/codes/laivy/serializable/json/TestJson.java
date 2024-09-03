package codes.laivy.serializable.json;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.json.adapter.JsonAdapters;
import com.google.gson.*;
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

        // Serialize primitives
        if (object instanceof Enum<?>) {
            return serialize((Enum<?>) object);
        } else if (object instanceof Enum<?>[]) {
            return serialize((Enum<?>[]) object);
        } else if (object instanceof Boolean) {
            return serialize((Boolean) object);
        } else if (object instanceof Boolean[]) {
            return serialize((Boolean[]) object);
        } else if (object instanceof Character) {
            return serialize((Character) object);
        } else if (object instanceof Character[]) {
            return serialize((Character[]) object);
        } else if (object instanceof Byte) {
            return serialize((Byte) object);
        } else if (object instanceof Byte[]) {
            return serialize((Byte[]) object);
        } else if (object instanceof Number[]) {
            return serialize((Number[]) object);
        } else if (object instanceof Number) {
            return serialize((Number) object);
        } else if (object instanceof boolean[]) {
            return serialize((boolean[]) object);
        } else if (object instanceof char[]) {
            return serialize((char[]) object);
        } else if (object instanceof byte[]) {
            return serialize((byte[]) object);
        } else if (object instanceof int[]) {
            return serialize((int[]) object);
        } else if (object instanceof short[]) {
            return serialize((short[]) object);
        } else if (object instanceof long[]) {
            return serialize((long[]) object);
        } else if (object instanceof float[]) {
            return serialize((float[]) object);
        } else if (object instanceof double[]) {
            return serialize((double[]) object);
        } else if (object instanceof String) {
            return serialize((String) object);
        } else if (object instanceof String[]) {
            return serialize((String[]) object);
        } else if (object instanceof Object[]) {
            return serialize((Object[]) object);
        }

        // Serialize object
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
            return adapter.deserialize(this, reference, element);
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
            } else if (reference == Boolean.class || reference == boolean.class) {
                return element.getAsBoolean();
            } else if (reference == Character.class || reference == char.class) {
                return element.getAsString().charAt(0);
            } else if (reference == Byte.class || reference == byte.class) {
                return element.getAsByte();
            } else if (reference == Short.class || reference == short.class) {
                return element.getAsShort();
            } else if (reference == Integer.class || reference == int.class) {
                return element.getAsInt();
            } else if (reference == Long.class || reference == long.class) {
                return element.getAsLong();
            } else if (reference == Float.class || reference == float.class) {
                return element.getAsFloat();
            } else if (reference == Double.class || reference == double.class) {
                return element.getAsDouble();
            } else {
                throw new UnsupportedOperationException("there's no primitive type with reference '" + reference + "', is missing any adapter here?");
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
            return (E) adapter.deserialize(this, reference, element);
        }

        if (JavaSerializableUtils.usesJavaSerialization(reference)) {
            return JavaSerializableUtils.javaDeserializeObject(this, reference, element);
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

}
