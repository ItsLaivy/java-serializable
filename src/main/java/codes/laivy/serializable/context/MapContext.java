package codes.laivy.serializable.context;

import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MapContext extends Context {

    // Object

    void setObject(@NotNull String name, @Nullable Object object);
    void setContext(@NotNull String name, @NotNull Context context);

    default <E> @Nullable E getObject(@NotNull Class<E> reference, @NotNull String name) {
        @Nullable Object object = getObject(References.of(reference), name);

        if (object != null && reference.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("cannot retrieve object from type '" + object.getClass().getName() + "' using '" + reference.getName() + "' reference");
        }

        //noinspection unchecked
        return (E) object;
    }
    @Nullable Object getObject(@NotNull References references, @NotNull String name);

    @NotNull Context getContext(@NotNull String name);

    boolean contains(@NotNull String name);

    @NotNull Set<@NotNull String> keySet();

    default int size() {
        return keySet().size();
    }
    default boolean isEmpty() {
        return size() == 0;
    }

}
