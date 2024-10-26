package codes.laivy.serializable;

import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
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

    @Override
    @NotNull Iterable<T> serialize(@Nullable Object @NotNull ... array);

    @Override
    @Nullable T serialize(@Nullable Serializable object, @NotNull Config config);
    @Override
    @Nullable T serialize(@Nullable Serializable object);
    @Override
    @NotNull Iterable<T> serialize(@Nullable Serializable @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(@NotNull Iterable<@Nullable Serializable> iterable, @NotNull Config config);

    // Enum

    @Override
    @Nullable T serialize(@Nullable Enum<?> e, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Enum<?> e);
    @Override
    @NotNull Iterable<T> serialize(@Nullable Enum<?> @NotNull ... array);

    // Boolean

    @Override
    @Nullable T serialize(@Nullable Boolean b, @NotNull Config config);
    @Override
    @NotNull T serialize(boolean b, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Boolean b);
    @Override
    @NotNull T serialize(boolean b);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Boolean @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(boolean @NotNull ... array);

    // Short

    @Override
    @Nullable T serialize(@Nullable Short s, @NotNull Config config);
    @Override
    @NotNull T serialize(short s, @NotNull Config config);

    @Override
    @NotNull T serialize(short s);
    @Override
    @Nullable T serialize(@Nullable Short s);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Short @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(short @NotNull ... array);

    // Integer

    @Override
    @Nullable T serialize(@Nullable Integer i, @NotNull Config config);
    @Override
    @NotNull T serialize(int i, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Integer i);
    @Override
    @NotNull T serialize(int i);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Integer @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(int @NotNull ... array);

    // Long

    @Override
    @Nullable T serialize(@Nullable Long l, @NotNull Config config);
    @Override
    @NotNull T serialize(long l, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Long l);
    @Override
    @NotNull T serialize(long l);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Long @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(long @NotNull ... array);

    // Float

    @Override
    @Nullable T serialize(@Nullable Float f, @NotNull Config config);
    @Override
    @NotNull T serialize(float f, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Float f);
    @Override
    @NotNull T serialize(float f);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Float @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(float @NotNull ... array);

    // Double

    @Override
    @Nullable T serialize(@Nullable Double d, @NotNull Config config);
    @Override
    @NotNull T serialize(double d, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Double d);
    @Override
    @NotNull T serialize(double d);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Double @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(double @NotNull ... array);

    // Character

    @Override
    @Nullable T serialize(@Nullable Character c, @NotNull Config config);
    @Override
    @NotNull T serialize(char c, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Character c);
    @Override
    @NotNull T serialize(char c);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Character @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(char @NotNull ... array);

    // Byte

    @Override
    @Nullable T serialize(@Nullable Byte b, @NotNull Config config);
    @Override
    @NotNull T serialize(byte b, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable Byte b);
    @Override
    @NotNull T serialize(byte b);

    @Override
    @NotNull Iterable<T> serialize(@Nullable Byte @NotNull ... array);
    @Override
    @NotNull Iterable<T> serialize(byte @NotNull ... array);

    // String

    @Override
    @Nullable T serialize(@Nullable String string, @NotNull Config config);

    @Override
    @Nullable T serialize(@Nullable String string);
    @Override
    @NotNull Iterable<T> serialize(@Nullable String @NotNull ... array);

    // Objects pure serialization

    @Override
    @Nullable T serialize(@Nullable Object object);
    @Override
    @Nullable T serialize(@Nullable Object object, @NotNull Config config);

    // Deserialization

    <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object) throws IncompatibleReferenceException;
    <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object, @NotNull Config config) throws IncompatibleReferenceException;

    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull [] array) throws IncompatibleReferenceException;

    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull Iterable<@Nullable T> iterable) throws IncompatibleReferenceException;
    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull Iterable<@Nullable T> iterable, @NotNull Config config) throws IncompatibleReferenceException;

    // Context

    @Nullable T serialize(@NotNull Context context);

}
