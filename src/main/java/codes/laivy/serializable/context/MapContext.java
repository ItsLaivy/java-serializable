package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MapContext extends Context {

    // Static initializers

    static @NotNull MapContext create(@NotNull Serializer serializer) {
        return new MapContextImpl(serializer);
    }

    // Object

    @NotNull Serializer getSerializer();

    void setContext(@NotNull String name, @NotNull Context context);
    @NotNull Context getContext(@NotNull String name);

    default void setObject(@NotNull String name, @Nullable Object object) {
        setObject(name, object, object != null ? Config.create(getSerializer(), object.getClass()) : Config.create());
    }
    default void setObject(@NotNull String name, @Nullable Object object, @NotNull Config config) {
        setObject(name, getSerializer().toContext(object, config));
    }
    // todo IncompatibleReferenceException
    default <E> @Nullable E getObject(@NotNull Class<E> reference, @NotNull String name) {
        return getSerializer().deserialize(reference, getContext(name));
    }

    boolean contains(@NotNull String name);

    @NotNull Set<@NotNull String> keySet();

    default int size() {
        return keySet().size();
    }
    default boolean isEmpty() {
        return size() == 0;
    }

}
