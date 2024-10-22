package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class MapContextImpl implements MapContext {

    // Object

    private final @NotNull Object lock = new Object();
    private final @NotNull Serializer serializer;

    private final @Nullable SerializationProperties properties;

    private final @NotNull Map<String, Context> contextMap = new HashMap<>();

    public MapContextImpl(@NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        this.serializer = serializer;
        this.properties = properties;
    }

    // Modules

    @Override
    public @NotNull Serializer getSerializer() {
        return serializer;
    }

    @Override
    public void setObject(@NotNull String name, @Nullable Object object) {
        synchronized (lock) {
            contextMap.put(name, serializer.toContext(object, properties));
        }
    }
    @Override
    public void setContext(@NotNull String name, @NotNull Context context) {
        synchronized (lock) {
            contextMap.put(name, context);
        }
    }

    @Override
    public @Nullable Object getObject(@NotNull References references, @NotNull String name) {
        synchronized (lock) {
            return serializer.deserialize(references, getContext(name), properties);
        }
    }
    @Override
    public @NotNull Context getContext(@NotNull String name) {
        if (contextMap.containsKey(name)) {
            return contextMap.get(name);
        } else {
            throw new IllegalArgumentException("there's no context with name '" + name + "'");
        }
    }

    @Override
    public boolean contains(@NotNull String name) {
        return contextMap.containsKey(name);
    }

    @Override
    public @NotNull Set<@NotNull String> keySet() {
        return contextMap.keySet();
    }

    // Properties

    @Override
    public @Nullable SerializationProperties getProperties() {
        return properties;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MapContextImpl)) return false;
        @NotNull MapContextImpl that = (MapContextImpl) object;
        return Objects.equals(getSerializer(), that.getSerializer()) && Objects.equals(getProperties(), that.getProperties()) && Objects.equals(contextMap, that.contextMap);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getSerializer(), getProperties(), contextMap);
    }

    @Override
    public @NotNull String toString() {
        return "MapContextImpl{" +
                "serializer=" + serializer +
                ", properties=" + properties +
                ", contextMap=" + contextMap +
                '}';
    }

}