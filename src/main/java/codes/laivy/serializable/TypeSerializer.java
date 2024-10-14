package codes.laivy.serializable;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.exception.MalformedClassException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;

// todo: In the java-serializable 1.2, all these Iterable methods will be changed to java.util.Collection
//       the only reason to still use that, is because the GSON JsonArray class only implements Iterable, but
//       i'm developing my own json library, that will made this.

// todo: #serialize(Object, OutputStream)
//       #deserialize(Class, InputStream)
public interface TypeSerializer<T> extends Serializer {

    // Adapters

    @NotNull Collection<Adapter> getAdapters();

    // Serializable

    @Nullable T serialize(@Nullable Serializable object) throws MalformedClassException;
    @NotNull Iterable<? extends T> serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException;
    @NotNull Iterable<? extends T> serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException;

    // Primitive

    @Nullable T serialize(@Nullable Enum<?> e);
    @NotNull Iterable<T> serialize(@Nullable Enum<?> @NotNull ... array);

    @Nullable T serialize(@Nullable Boolean b);
    @NotNull Iterable<T> serialize(@Nullable Boolean @NotNull ... array);

    @Nullable T serialize(@Nullable Short s);
    @NotNull Iterable<T> serialize(@Nullable Short @NotNull ... array);

    @Nullable T serialize(@Nullable Integer i);
    @NotNull Iterable<T> serialize(@Nullable Integer @NotNull ... array);

    @Nullable T serialize(@Nullable Long l);
    @NotNull Iterable<T> serialize(@Nullable Long @NotNull ... array);

    @Nullable T serialize(@Nullable Float f);
    @NotNull Iterable<T> serialize(@Nullable Float @NotNull ... array);

    @Nullable T serialize(@Nullable Double d);
    @NotNull Iterable<T> serialize(@Nullable Double @NotNull ... array);

    @Nullable T serialize(@Nullable Character c);
    @NotNull Iterable<T> serialize(@Nullable Character @NotNull ... array);

    @Nullable T serialize(@Nullable Byte b);
    @NotNull Iterable<T> serialize(@Nullable Byte @NotNull ... array);

    @Nullable T serialize(@Nullable String string);
    @NotNull Iterable<T> serialize(@Nullable String @NotNull ... array);

    @NotNull T serialize(boolean b);
    @NotNull T serialize(char c);
    @NotNull T serialize(byte b);
    @NotNull T serialize(short s);
    @NotNull T serialize(int i);
    @NotNull T serialize(long l);
    @NotNull T serialize(float f);
    @NotNull T serialize(double d);

    @NotNull Iterable<T> serialize(boolean @NotNull ... array);
    @NotNull Iterable<T> serialize(char @NotNull ... array);
    @NotNull Iterable<T> serialize(byte @NotNull ... array);
    @NotNull Iterable<T> serialize(short @NotNull ... array);
    @NotNull Iterable<T> serialize(int @NotNull ... array);
    @NotNull Iterable<T> serialize(long @NotNull ... array);
    @NotNull Iterable<T> serialize(float @NotNull ... array);
    @NotNull Iterable<T> serialize(double @NotNull ... array);

    // Objects pure serialization

    @Nullable T serialize(@Nullable Object object) throws MalformedClassException;
    @NotNull Iterable<T> serialize(@Nullable Object @NotNull ... array) throws MalformedClassException;

    // Deserialization

    <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object) throws MalformedClassException;
    <E> @NotNull Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull ... array) throws MalformedClassException;

}
