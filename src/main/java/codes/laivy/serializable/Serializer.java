package codes.laivy.serializable;

import codes.laivy.serializable.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

// todo: In the java-serializable 1.2, all these Iterable methods will be changed to java.util.Collection
//       the only reason to still use that, is because the GSON JsonArray class only implements Iterable, but
//       i'm developing my own json library, that will made this.

public interface Serializer<T> {

    // Adapters

    @NotNull Adapters<T> getAdapters();

    // Serializable

    @Nullable T serialize(@Nullable Serializable object) throws InvalidClassException;
    @Nullable Iterable<? extends T> serialize(@Nullable Serializable @NotNull ... array) throws InvalidClassException;
    @NotNull Iterable<? extends T> serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws InvalidClassException;

    // Primitive

    @Nullable T serialize(@Nullable Enum<?> e);
    @NotNull Iterable<T> serialize(@Nullable Enum<?> @NotNull ... array);

    @Nullable T serialize(@Nullable Boolean b);
    @NotNull Iterable<T> serialize(@Nullable Boolean @NotNull ... array);

    @Nullable T serialize(@Nullable Number number);
    @NotNull Iterable<T> serialize(@Nullable Number @NotNull ... array);

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

    @Nullable T serialize(@Nullable Object object) throws InvalidClassException;
    @NotNull Iterable<T> serialize(@Nullable Object @NotNull ... array) throws InvalidClassException;

    // Deserialization

    <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T object) throws InvalidClassException;
    <E> @NotNull Collection<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull ... array) throws InvalidClassException;

    // Classes

    interface Adapters<T> extends Iterable<Adapter<T, ?>> {

        <E> void put(@NotNull Adapter<T, E> adapter);
        <E> boolean add(@NotNull Adapter<T, E> adapter);

        <E> boolean remove(@NotNull Class<E> reference);

        boolean contains(@NotNull Class<?> reference);
        boolean contains(@NotNull Adapter<T, ?> adapter);

        <E> @NotNull Optional<Adapter<T, E>> get(@NotNull Class<E> reference);

        int size();
        void clear();

    }

}
