package codes.laivy.serializable.context;

import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.Collection;
import java.util.Objects;

public interface ArrayContext extends Context, Collection<Context> {

    // Object

    default <E> @Nullable E readObject(@NotNull Class<E> reference) throws EOFException {
        @Nullable Object object = readObject(reference);

        if (object != null && reference.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("cannot retrieve object from type '" + object.getClass().getName() + "' using '" + reference.getName() + "' reference");
        }

        //noinspection unchecked
        return (E) object;
    }
    @Nullable Object readObject(@NotNull References references) throws EOFException;

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

    void write(@Nullable Object object);
    void write(@NotNull Context context);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

}
