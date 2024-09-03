package codes.laivy.serializable.json;

import codes.laivy.serializable.adapter.Adapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class JsonUtilities {

    // Static initializers

    @SuppressWarnings({"DataFlowIssue", "rawtypes", "unchecked"})
    public static @NotNull JsonElement serializeObject(@NotNull JsonSerializable serializer, @NotNull Object object, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
        if (object.getClass().isArray()) {
            throw new InvalidClassException("cannot deserialize an array object using this method");
        }

        // Adapter
        @Nullable Adapter adapter = serializer.getAdapters().get(object.getClass()).orElse(null);

        if (adapter != null) {
            return (JsonElement) adapter.serialize(serializer, object);
        }

        // Java serialization
        if (JavaSerializableUtils.usesJavaSerialization(object.getClass())) {
            return JavaSerializableUtils.javaSerializeObject(serializer, object);
        }

        // Strict classes
        if (object.getClass() == Class.class) { // Class
            return serializer.serialize(((Class<?>) object).getCanonicalName());
        }

        // Start serialization
        @NotNull JsonObject json = new JsonObject();

        // Start serializes the fields
        @NotNull Class<?> type = object.getClass();

        // Start looking fields into class and superclasses
        while (type != Object.class) {
            for (@NotNull Entry<String, Field> entry : FieldUtils.getFields(type).entrySet()) {
                @NotNull String name = entry.getKey();
                @NotNull Field field = entry.getValue();

                serializeField(serializer, json, object, field, name, map);
            }

            // Finish with the superclass
            type = type.getSuperclass();
        }

        return json;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void serializeField(@NotNull JsonSerializable serializer, @NotNull JsonObject object, @NotNull Object instance, @NotNull Field field, @NotNull String name, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
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

            // Serialize field value and add it to JSON
            @Nullable JsonElement element;
            @Nullable Adapter adapter = value == null ? null : serializer.getAdapters().get(value.getClass()).orElse(null);

            if (adapter != null) {
                element = (JsonElement) adapter.serialize(serializer, value);
            } else if (value == null) {
                element = JsonNull.INSTANCE;
            } else if (value instanceof Enum<?>) {
                element = serializer.serialize((Enum<?>) value);
            } else if (value instanceof Enum<?>[]) {
                element = serializer.serialize((Enum<?>[]) value);
            } else if (value instanceof Boolean) {
                element = serializer.serialize((Boolean) value);
            } else if (value instanceof Boolean[]) {
                element = serializer.serialize((Boolean[]) value);
            } else if (value instanceof Character) {
                element = serializer.serialize((Character) value);
            } else if (value instanceof Character[]) {
                element = serializer.serialize((Character[]) value);
            } else if (value instanceof Byte) {
                element = serializer.serialize((Byte) value);
            } else if (value instanceof Byte[]) {
                element = serializer.serialize((Byte[]) value);
            } else if (value instanceof Number[]) {
                element = serializer.serialize((Number[]) value);
            } else if (value instanceof Number) {
                element = serializer.serialize((Number) value);
            } else if (value instanceof boolean[]) {
                element = serializer.serialize((boolean[]) value);
            } else if (value instanceof char[]) {
                element = serializer.serialize((char[]) value);
            } else if (value instanceof byte[]) {
                element = serializer.serialize((byte[]) value);
            } else if (value instanceof int[]) {
                element = serializer.serialize((int[]) value);
            } else if (value instanceof short[]) {
                element = serializer.serialize((short[]) value);
            } else if (value instanceof long[]) {
                element = serializer.serialize((long[]) value);
            } else if (value instanceof float[]) {
                element = serializer.serialize((float[]) value);
            } else if (value instanceof double[]) {
                element = serializer.serialize((double[]) value);
            } else if (value instanceof String) {
                element = serializer.serialize((String) value);
            } else if (value instanceof String[]) {
                element = serializer.serialize((String[]) value);
            } else if (value instanceof Object[]) {
                element = serializer.serialize((Object[]) value);
            } else {
                element = serializeObject(serializer, value, map);
            }

            object.add(name, element);

            // Make it inaccessible again
            field.setAccessible(accessible);
        } catch (@NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot access field '" + field.getName() + "' to proceed serialization", e);
        }
    }

    // Object

    private JsonUtilities() {
        throw new UnsupportedOperationException();
    }

}
