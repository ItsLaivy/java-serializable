package codes.laivy.serializable.json;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.KnownAs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

final class JsonUtilities {

    // Static initializers

    /**
     * Esse método é para verificar se a serialização deve ocorrer usando a serialização nativa do java ou a da biblioteca.
     * Quando a serialização nativa do java é usada, o json retornado pela biblioteca é um json array contendo os dados do ObjectStream.
     *
     * Um objeto deve usar a serialização nativa do java quando ele possui uma ou mais dessas características, aplicando também para suas super classes:
     * 1. Possui o método #writeObject(java.io.ObjectOutputStream out)
     * 2. Possui o método #readObject(java.io.ObjectInputStream in)
     * 3. Possui o método #readObjectNoData()
     * 4. Possui o método #writeReplace()
     * 5. Possui o método #readResolve()
     * 6. Implementa a interface {@link java.io.Externalizable}
     *
     * Não é possível serializar um objeto adaptado para a serialização nativa do java de forma bonita (tipo {"field":"value"}) pois esses objetos
     * possuem integração direta com os ObjectInputs e ObjectInputStreams, não é possível obter o nome dos campos nesse tipo de serialização, logo a
     * única forma de se serializar isso, é salvando os bytes diretos.
     * <p>
     * Ainda é possível serializar um objeto que está adaptado para a serialização nativa de forma bonita usando {@link Adapter}
     *
     * @param c
     * @return
     */
    public static boolean usesJavaSerialization(final @NotNull Class<?> c) {
        if (Externalizable.class.isAssignableFrom(c)) {
            return true;
        }

        boolean methods = false;
        @NotNull Class<?> copy = c;

        while (copy != Object.class) {
            @NotNull Method method;

            try {
                method = copy.getDeclaredMethod("writeObject", ObjectOutputStream.class);
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            } try {
                method = copy.getDeclaredMethod("readObject", ObjectInputStream.class);
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            } try {
                method = copy.getDeclaredMethod("readObjectNoData");
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            } try {
                method = copy.getDeclaredMethod("writeReplace");
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            } try {
                method = copy.getDeclaredMethod("readResolve");
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            }

            copy = copy.getSuperclass();
        }

        if (methods && !Serializable.class.isAssignableFrom(c)) {
            throw new IllegalStateException("the class '" + c + "' have serialization methods but doesn't implements Serializable interface");
        }

        return methods;
    }

    public static @NotNull JsonArray javaSerializeObject(@NotNull Object object) {
        try {
            @NotNull ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            @NotNull ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(object);

            @NotNull JsonArray array = new JsonArray();

            for (byte b : bytes.toByteArray()) {
                array.add(b);
            }

            return array;
        } catch (@NotNull IOException e) {
            throw new RuntimeException("cannot serialize", e);
        }
    }
    @SuppressWarnings({"DataFlowIssue", "rawtypes", "unchecked"})
    public static @NotNull JsonElement serializeObject(@NotNull TestJson serializer, @NotNull Object object, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
        if (object.getClass().isArray()) {
            throw new InvalidClassException("cannot deserialize an array object using this method");
        }

        // Adapter
        @Nullable Adapter adapter = serializer.getAdapters().get(object.getClass()).orElse(null);

        if (adapter != null) {
            return (JsonElement) adapter.serialize(object);
        }

        // Java serialization
        if (usesJavaSerialization(object.getClass())) {
            return javaSerializeObject(object);
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
            for (@NotNull Field field : getFields(type)) {
                serializeField(serializer, json, object, field, map);
            }

            // Finish with the superclass
            type = type.getSuperclass();
        }

        return json;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void serializeField(@NotNull TestJson serializer, @NotNull JsonObject object, @NotNull Object instance, @NotNull Field field, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
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
            @Nullable JsonElement element;
            @Nullable Adapter adapter = value == null ? null : serializer.getAdapters().get(value.getClass()).orElse(null);

            if (adapter != null) {
                element = (JsonElement) adapter.serialize(value);
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
            } else if (value instanceof Byte) {
                element = serializer.serialize((Byte) value);
            } else if (value instanceof Byte[]) {
                element = serializer.serialize((Byte[]) value);
            } else if (value instanceof Short) {
                element = serializer.serialize((Short) value);
            } else if (value instanceof Float) {
                element = serializer.serialize((Float) value);
            } else if (value instanceof Double) {
                element = serializer.serialize((Double) value);
            } else if (value instanceof Number) {
                element = serializer.serialize((Number) value);
            } else if (value instanceof Number[]) {
                element = serializer.serialize((Number[]) value);
            } else if (value instanceof Character) {
                element = serializer.serialize((Character) value);
            } else if (value instanceof Character[]) {
                element = serializer.serialize((Character[]) value);
            } else if (value instanceof char[]) {
                element = serializer.serialize((char[]) value);
            } else if (value instanceof boolean[]) {
                element = serializer.serialize((boolean[]) value);
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

    static @NotNull Field[] getFields(@NotNull Class<?> type) {
        return Stream.of(type.getFields(), type.getDeclaredFields()).flatMap(Arrays::stream).distinct().toArray(Field[]::new);
    }
    static @Nullable Field getFieldByName(@NotNull Object object, @NotNull String name) {
        for (@NotNull Field field : getFields(object.getClass())) {
            // Skip transient and static fields
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // Check and get by @KnownAs, will use the name otherwise.
            if (field.isAnnotationPresent(KnownAs.class)) {
                @NotNull KnownAs annotation = field.getAnnotation(KnownAs.class);

                if (annotation.name().equals(name)) {
                    return field;
                }
            } else if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    // Object

    private JsonUtilities() {
        throw new UnsupportedOperationException();
    }

}
