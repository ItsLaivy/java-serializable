package codes.laivy.serializable.json;

import codes.laivy.serializable.exception.MalformedClassException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class JavaSerializableUtils {

    // Static initializers

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
            throw new IllegalStateException("the class '" + c + "' has serialization methods but doesn't implement Serializable interface");
        }

        return methods;
    }

    public static <E> @Nullable E javaDeserializeObject(@NotNull Class<E> reference, @Nullable JsonElement element) throws MalformedClassException {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        // Byte array
        byte[] bytes;

        if (element.isJsonArray()) {
            @NotNull JsonArray array = element.getAsJsonArray();
            bytes = new byte[array.size()];

            int row = 0;
            for (@NotNull JsonElement e : array) {
                bytes[row] = e.getAsByte();
                row++;
            }
        } else {
            throw new IllegalArgumentException("cannot deserialize '" + reference + "' object with json '" + element + "'. Is missing any adapter?");
        }

        // Deserialize using object input stream
        try {
            // Read input stream
            @NotNull ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            //noinspection unchecked
            return (E) stream.readObject();
        } catch (@NotNull IOException | @NotNull ClassNotFoundException e) {
            throw new RuntimeException("an unknown error occurred trying to deserialize reference '" + reference + "' with json data '" + element + "'", e);
        }
    }

    public static @Nullable JsonElement javaSerializeObject(@Nullable Object object) {
        try {
            @NotNull ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            @NotNull ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(object);

            // Byte array adapter
            @NotNull JsonArray array = new JsonArray();

            for (byte b : bytes.toByteArray()) {
                array.add(b);
            }

            return array;
        } catch (@NotNull IOException e) {
            throw new RuntimeException("cannot serialize", e);
        }
    }

    // Object

    /**
     * Private constructor to prevent instantiation.
     * This class is intended to be a utility class and cannot be instantiated.
     */
    private JavaSerializableUtils() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }
}