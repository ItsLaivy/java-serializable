package codes.laivy.serializable.json;

import codes.laivy.serializable.adapter.Adapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A utility class for handling methods related to Java's native serialization.
 * This class provides utility methods to determine if a class uses Java native serialization
 * and to serialize and deserialize objects using Java native serialization.
 * <p>
 * Native Java serialization uses mechanisms like {@code writeObject}, {@code readObject},
 * {@code readObjectNoData}, {@code writeReplace}, and {@code readResolve} methods, as well as
 * the {@link java.io.Externalizable} interface.
 * <p>
 * Objects adapted for Java native serialization are represented as byte streams, and cannot be
 * serialized into a readable JSON format with field names and values. The only way to serialize
 * such objects is to directly store their byte representation.
 * <p>
 * It is still possible to serialize an object adapted for Java native serialization into a readable
 * format using {@link Adapter}.
 * <p>
 * The class is not intended to be instantiated.
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
final class JavaSerializableUtils {

    // Static initializers

    /**
     * Determines if the given class uses Java native serialization.
     * <p>
     * A class is considered to use Java native serialization if it has one or more of the following methods:
     * <ul>
     *   <li>{@code writeObject(java.io.ObjectOutputStream out)}</li>
     *   <li>{@code readObject(java.io.ObjectInputStream in)}</li>
     *   <li>{@code readObjectNoData()}</li>
     *   <li>{@code writeReplace()}</li>
     *   <li>{@code readResolve()}</li>
     * </ul>
     * or implements {@link java.io.Externalizable}.
     * <p>
     * The class must also implement {@link java.io.Serializable} if it has serialization methods.
     *
     * @param c the class to check
     * @return {@code true} if the class uses Java native serialization, {@code false} otherwise
     * @throws IllegalStateException if the class has serialization methods but does not implement {@link java.io.Serializable}
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
            throw new IllegalStateException("the class '" + c + "' has serialization methods but doesn't implement Serializable interface");
        }

        return methods;
    }

    /**
     * Deserializes an object from JSON using Java native serialization.
     * <p>
     * This method first converts the JSON element to a byte array. It then uses an
     * {@link ObjectInputStream} to read the object from the byte array.
     * <p>
     * The JSON element can be a Base64-encoded string or a JSON array of bytes.
     *
     * @param serializer the serializer used to get the byte array adapter
     * @param reference the class of the object to deserialize
     * @param element the JSON element containing the serialized data
     * @param <E> the type of the object to deserialize
     * @return the deserialized object, or {@code null} if the JSON element is {@code null} or empty
     * @throws InvalidClassException if deserialization fails due to class issues
     * @throws RuntimeException if deserialization fails due to IO or class not found issues
     */
    public static <E> @Nullable E javaDeserializeObject(@NotNull JsonSerializable serializer, @NotNull Class<E> reference, @Nullable JsonElement element) throws InvalidClassException {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        // Byte array adapter
        @Nullable Adapter<JsonElement, byte[]> adapter = serializer.getAdapters().get(byte[].class).orElse(null);
        byte[] bytes;

        if (adapter != null) {
            bytes = adapter.deserialize(serializer, byte[].class, element);
        } else if (element.isJsonArray()) {
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
            // Check nullability
            if (bytes == null) {
                return null;
            }

            // Read input stream
            @NotNull ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            //noinspection unchecked
            return (E) stream.readObject();
        } catch (@NotNull IOException | @NotNull ClassNotFoundException e) {
            throw new RuntimeException("an unknown error occurred trying to deserialize reference '" + reference + "' with json data '" + element + "'", e);
        }
    }

    /**
     * Serializes an object into JSON using Java native serialization.
     * <p>
     * This method uses an {@link ObjectOutputStream} to write the object to a byte array.
     * The byte array is then either encoded using a Base64 adapter or represented as a JSON array of bytes.
     *
     * @param serializer the serializer used to get the byte array adapter
     * @param object the object to serialize
     * @return the JSON element representing the serialized object, or {@code null} if the object is {@code null}
     * @throws RuntimeException if serialization fails due to IO issues
     */
    public static @Nullable JsonElement javaSerializeObject(@NotNull JsonSerializable serializer, @Nullable Object object) {
        try {
            @NotNull ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            @NotNull ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(object);

            // Byte array adapter
            @Nullable Adapter<JsonElement, byte[]> adapter = serializer.getAdapters().get(byte[].class).orElse(null);
            @Nullable JsonElement element;

            if (adapter != null) {
                return adapter.serialize(serializer, bytes.toByteArray());
            } else {
                @NotNull JsonArray array = new JsonArray();

                for (byte b : bytes.toByteArray()) {
                    array.add(b);
                }

                return array;
            }
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