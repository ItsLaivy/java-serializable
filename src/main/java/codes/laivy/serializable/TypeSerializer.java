package codes.laivy.serializable;

import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

// todo: In the java-serializable 1.2, all these Iterable methods will be changed to java.util.Collection
//       the only reason to still use that, is because the GSON JsonArray class only implements Iterable, but
//       i'm developing my own json library, that will made this.

// todo: #serialize(Object, OutputStream)
//       #deserialize(Class, InputStream)
public interface TypeSerializer<T> extends Serializer {

    // Serializable

    @Nullable T serialize(@Nullable Serializable object, @Nullable SerializationProperties properties) throws MalformedClassException;
    @Nullable T serialize(@Nullable Serializable object) throws MalformedClassException;

    @NotNull Iterable<T> serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException;
    @NotNull Iterable<T> serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException;
    @NotNull Iterable<T> serialize(@Nullable Object @NotNull ... array) throws MalformedClassException;

    // Enum

    @Nullable T serialize(@Nullable Enum<?> e, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Enum<?> e);
    @NotNull Iterable<T> serialize(@Nullable Enum<?> @NotNull ... array);

    // Boolean

    @Nullable T serialize(@Nullable Boolean b, @Nullable SerializationProperties properties);
    @NotNull T serialize(boolean b, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Boolean b);
    @NotNull T serialize(boolean b);

    @NotNull Iterable<T> serialize(@Nullable Boolean @NotNull ... array);
    @NotNull Iterable<T> serialize(boolean @NotNull ... array);

    // Short

    @Nullable T serialize(@Nullable Short s, @Nullable SerializationProperties properties);
    @NotNull T serialize(short s, @Nullable SerializationProperties properties);

    @NotNull T serialize(short s);
    @Nullable T serialize(@Nullable Short s);

    @NotNull Iterable<T> serialize(@Nullable Short @NotNull ... array);
    @NotNull Iterable<T> serialize(short @NotNull ... array);

    // Integer

    @Nullable T serialize(@Nullable Integer i, @Nullable SerializationProperties properties);
    @NotNull T serialize(int i, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Integer i);
    @NotNull T serialize(int i);

    @NotNull Iterable<T> serialize(@Nullable Integer @NotNull ... array);
    @NotNull Iterable<T> serialize(int @NotNull ... array);

    // Long

    @Nullable T serialize(@Nullable Long l, @Nullable SerializationProperties properties);
    @NotNull T serialize(long l, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Long l);
    @NotNull T serialize(long l);

    @NotNull Iterable<T> serialize(@Nullable Long @NotNull ... array);
    @NotNull Iterable<T> serialize(long @NotNull ... array);

    // Float

    @Nullable T serialize(@Nullable Float f, @Nullable SerializationProperties properties);
    @NotNull T serialize(float f, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Float f);
    @NotNull T serialize(float f);

    @NotNull Iterable<T> serialize(@Nullable Float @NotNull ... array);
    @NotNull Iterable<T> serialize(float @NotNull ... array);

    // Double

    @Nullable T serialize(@Nullable Double d, @Nullable SerializationProperties properties);
    @NotNull T serialize(double d, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Double d);
    @NotNull T serialize(double d);

    @NotNull Iterable<T> serialize(@Nullable Double @NotNull ... array);
    @NotNull Iterable<T> serialize(double @NotNull ... array);

    // Character

    @Nullable T serialize(@Nullable Character c, @Nullable SerializationProperties properties);
    @NotNull T serialize(char c, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Character c);
    @NotNull T serialize(char c);

    @NotNull Iterable<T> serialize(@Nullable Character @NotNull ... array);
    @NotNull Iterable<T> serialize(char @NotNull ... array);

    // Byte

    @Nullable T serialize(@Nullable Byte b, @Nullable SerializationProperties properties);
    @NotNull T serialize(byte b, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable Byte b);
    @NotNull T serialize(byte b);

    @NotNull Iterable<T> serialize(@Nullable Byte @NotNull ... array);
    @NotNull Iterable<T> serialize(byte @NotNull ... array);

    // String

    @Nullable T serialize(@Nullable String string, @Nullable SerializationProperties properties);

    @Nullable T serialize(@Nullable String string);
    @NotNull Iterable<T> serialize(@Nullable String @NotNull ... array);

    // Objects pure serialization

    @Nullable T serialize(@Nullable Object object) throws MalformedClassException;
    @Nullable T serialize(@Nullable Object object, @Nullable SerializationProperties properties);

    // Deserialization

    <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object) throws MalformedClassException;

    @Nullable Object deserialize(@NotNull References references, @Nullable T object) throws MalformedClassException;
    @Nullable Object deserialize(@NotNull References references, @Nullable T object, @Nullable SerializationProperties properties) throws MalformedClassException;

    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull [] array) throws MalformedClassException;
    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull Iterable<@Nullable T> array) throws MalformedClassException;

    // Context

    @Nullable T serialize(@NotNull Context context);

}
