package codes.laivy.serializable;

import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

// todo: In the java-serializable 1.2, all these Iterable methods will be changed to java.util.Collection
//       the only reason to still use that, is because the GSON JsonArray class only implements Iterable, but
//       i'm developing my own json library, that will made this.

// todo: #serialize(Object, OutputStream)
//       #deserialize(Class, InputStream)
public interface TypeSerializer<T> extends Serializer {

    // Serializable

    @Nullable T serialize(@Nullable Serializable object, @Nullable SerializationProperties properties) throws MalformedClassException;
    default @Nullable T serialize(@Nullable Serializable object) throws MalformedClassException {
        return serialize(object, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Serializable object : array) {
            list.add(serialize(object));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Serializable object : iterable) {
            list.add(serialize(object));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(@Nullable Object @NotNull ... array) throws MalformedClassException {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Object object : array) {
            list.add(serialize(object));
        }

        return list;
    }

    // Enum

    @Nullable T serialize(@Nullable Enum<?> e, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Enum<?> e) {
        return serialize(e, null);
    }
    default @NotNull Iterable<T> serialize(@Nullable Enum<?> @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Enum<?> e : array) {
            list.add(serialize(e));
        }

        return list;
    }

    // Boolean

    @Nullable T serialize(@Nullable Boolean b, @Nullable SerializationProperties properties);
    @NotNull T serialize(boolean b, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Boolean b) {
        return serialize(b, null);
    }
    default @NotNull T serialize(boolean b) {
        return serialize(b, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Boolean @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Boolean bool : array) {
            list.add(serialize(bool));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(boolean @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (boolean bool : array) {
            list.add(serialize(bool));
        }

        return list;
    }

    // Short

    @Nullable T serialize(@Nullable Short s, @Nullable SerializationProperties properties);
    @NotNull T serialize(short s, @Nullable SerializationProperties properties);

    default @NotNull T serialize(short s) {
        return serialize(s, null);
    }
    default @Nullable T serialize(@Nullable Short s) {
        return serialize(s, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Short @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Short s : array) {
            list.add(serialize(s));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(short @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (short s : array) {
            list.add(serialize(s));
        }

        return list;
    }

    // Integer

    @Nullable T serialize(@Nullable Integer i, @Nullable SerializationProperties properties);
    @NotNull T serialize(int i, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Integer i) {
        return serialize(i, null);
    }
    default @NotNull T serialize(int i) {
        return serialize(i, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Integer @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Integer i : array) {
            list.add(serialize(i));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(int @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (int i : array) {
            list.add(serialize(i));
        }

        return list;
    }

    // Long

    @Nullable T serialize(@Nullable Long l, @Nullable SerializationProperties properties);
    @NotNull T serialize(long l, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Long l) {
        return serialize(l, null);
    }
    default @NotNull T serialize(long l) {
        return serialize(l, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Long @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Long l : array) {
            list.add(serialize(l));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(long @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (long l : array) {
            list.add(serialize(l));
        }

        return list;
    }

    // Float

    @Nullable T serialize(@Nullable Float f, @Nullable SerializationProperties properties);
    @NotNull T serialize(float f, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Float f) {
        return serialize(f, null);
    }
    default @NotNull T serialize(float f) {
        return serialize(f, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Float @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Float f : array) {
            list.add(serialize(f));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(float @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (float f : array) {
            list.add(serialize(f));
        }

        return list;
    }

    // Double

    @Nullable T serialize(@Nullable Double d, @Nullable SerializationProperties properties);
    @NotNull T serialize(double d, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Double d) {
        return serialize(d, null);
    }
    default @NotNull T serialize(double d) {
        return serialize(d, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Double @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Double d : array) {
            list.add(serialize(d));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(double @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (double d : array) {
            list.add(serialize(d));
        }

        return list;
    }

    // Character

    @Nullable T serialize(@Nullable Character c, @Nullable SerializationProperties properties);
    @NotNull T serialize(char c, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Character c) {
        return serialize(c, null);
    }
    default @NotNull T serialize(char c) {
        return serialize(c, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Character @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Character c : array) {
            list.add(serialize(c));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(char @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (char c : array) {
            list.add(serialize(c));
        }

        return list;
    }

    // Byte

    @Nullable T serialize(@Nullable Byte b, @Nullable SerializationProperties properties);
    @NotNull T serialize(byte b, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable Byte b) {
        return serialize(b, null);
    }
    default @NotNull T serialize(byte b) {
        return serialize(b, null);
    }

    default @NotNull Iterable<T> serialize(@Nullable Byte @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Byte b : array) {
            list.add(serialize(b));
        }

        return list;
    }
    default @NotNull Iterable<T> serialize(byte @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (byte b : array) {
            list.add(serialize(b));
        }

        return list;
    }

    // String

    @Nullable T serialize(@Nullable String string, @Nullable SerializationProperties properties);

    default @Nullable T serialize(@Nullable String string) {
        return serialize(string, null);
    }
    @NotNull Iterable<T> serialize(@Nullable String @NotNull ... array);

    // Objects pure serialization

    @Nullable T serialize(@Nullable Object object) throws MalformedClassException;
    @Nullable T serialize(@Nullable Object object, @Nullable SerializationProperties properties);

    // Deserialization

    default <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object) throws MalformedClassException {
        return deserialize(reference, object, null);
    }
    <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object, @Nullable SerializationProperties properties) throws MalformedClassException;

    default @Nullable Object deserialize(@NotNull References references, @Nullable T object) throws MalformedClassException {
        return deserialize(references, object, null);
    }
    @Nullable Object deserialize(@NotNull References references, @Nullable T object, @Nullable SerializationProperties properties) throws MalformedClassException;

    default <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull [] array) throws MalformedClassException {
        return deserialize(reference, array, null);
    }
    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull [] array, @Nullable SerializationProperties properties) throws MalformedClassException;

    // Context

    @Nullable T serialize(@NotNull Context context);

}
