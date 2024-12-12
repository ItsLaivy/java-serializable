package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@MethodSerialization
final class MapContextImpl implements MapContext {

    // Object

    private final @NotNull Object lock = new Object();
    private final @NotNull Serializer serializer;

    private final @NotNull Map<String, Context> map;

    public MapContextImpl(@NotNull Serializer serializer) {
        this.serializer = serializer;
        this.map = new LinkedHashMap<>();
    }
    public MapContextImpl(@NotNull Map<String, Context> map) {
        this.serializer = JsonSerializer.getInstance();
        this.map = map;
    }

    // Modules

    @Override
    public @NotNull Serializer getSerializer() {
        return serializer;
    }

    @Override
    public void setContext(@NotNull String name, @NotNull Context context) {
        synchronized (lock) {
            map.put(name, context);
        }
    }
    @Override
    public @NotNull Context getContext(@NotNull String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        } else {
            throw new IllegalArgumentException("there's no context with name '" + name + "'");
        }
    }

    @Override
    public @Nullable Context remove(@NotNull String name) {
        return map.remove(name);
    }

    @Override
    public boolean contains(@NotNull String name) {
        return map.containsKey(name);
    }

    @Override
    public @NotNull Set<@NotNull String> keySet() {
        return map.keySet();
    }

    @Override
    public @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull Context>> entrySet() {
        return map.entrySet();
    }

    @Override
    public @NotNull Collection<@NotNull Context> values() {
        return map.values();
    }

    // Serializers

    private static @NotNull Map<String, Context> serialize(@NotNull MapContextImpl context) {
        return context.map;
    }
    private static @NotNull MapContextImpl deserialize(@NotNull Map<String, Context> map) {
        return new MapContextImpl(map);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MapContextImpl)) return false;
        @NotNull MapContextImpl that = (MapContextImpl) object;
        return Objects.equals(getSerializer(), that.getSerializer()) && Objects.equals(map, that.map);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getSerializer(), map);
    }

    @Override
    public @NotNull String toString() {
        return map.toString();
    }

}
