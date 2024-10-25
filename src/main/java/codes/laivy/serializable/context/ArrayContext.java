package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.Collection;
import java.util.Objects;

// todo: writeAll
public interface ArrayContext extends Context, Collection<Context> {

    // Static initializers

    static @NotNull ArrayContext create(@NotNull Serializer serializer) {
        return new ArrayContextImpl(serializer);
    }

    // Object

    @NotNull Serializer getSerializer();

    default <E> @Nullable E readObject(@NotNull Class<E> reference) throws EOFException {
        return getSerializer().deserialize(reference, readContext());
    }

    @NotNull Context readContext() throws EOFException;

    default boolean readBoolean() throws EOFException {
        return Boolean.TRUE.equals(readObject(boolean.class));
    }
    default byte readByte() throws EOFException {
        return Objects.requireNonNull(readObject(byte.class));
    }
    default short readShort() throws EOFException {
        return Objects.requireNonNull(readObject(short.class));
    }
    default char readChar() throws EOFException {
        return Objects.requireNonNull(readObject(char.class));
    }
    default int readInt() throws EOFException {
        return Objects.requireNonNull(readObject(int.class));
    }
    default long readLong() throws EOFException {
        return Objects.requireNonNull(readObject(long.class));
    }
    default float readFloat() throws EOFException {
        return Objects.requireNonNull(readObject(float.class));
    }
    default double readDouble() throws EOFException {
        return Objects.requireNonNull(readObject(double.class));
    }
    default @Nullable String readString() throws EOFException {
        return readObject(String.class);
    }

    default void write(@Nullable Object object) {
        write(object, object != null ? Config.create(getSerializer(), object.getClass()) : Config.create());
    }
    default void write(@Nullable Object object, @NotNull Config config) {
        write(getSerializer().toContext(object, config));
    }

    void write(@NotNull Context context);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

}
