package codes.laivy.serializable.json.adapter;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer.Adapters;
import codes.laivy.serializable.adapter.Adapter;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

public class JsonAdapters implements Adapters<JsonElement> {

    // Static initializers

    private static final @NotNull Object lock = new Object();
    private static final @NotNull Set<JsonAdapter<?>> adapters = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger(JsonAdapters.class);

    // This method is NOT thread-safe!
    public static @NotNull Collection<JsonAdapter<?>> getDefaultAdapters() {
        return adapters;
    }

    /**
     * Creates an empty iterable of JSON adapters, without any default adapters.
     *
     * @return an empty JSON adapters collection
     */
    public static @NotNull JsonAdapters empty() {
        return Allocator.allocate(JsonAdapters.class);
    }

    static {
        @NotNull Reflections reflections = new Reflections("codes.laivy.serializable.json.adapter");
        for (Class<?> reference : reflections.getSubTypesOf(JsonAdapter.class)) {
            if (reference == JsonAdapter.class) {
                continue;
            }

            // Generate adapter
            @NotNull JsonAdapter<?> adapter;

            try {
                @NotNull Constructor<?> constructor = reference.getConstructor();
                constructor.setAccessible(true);

                adapter = (JsonAdapter<?>) constructor.newInstance();
            } catch (@NotNull NoSuchMethodException ignore) {
                adapter = (JsonAdapter<?>) Allocator.allocate(reference);
            } catch (@NotNull Throwable throwable) {
                log.error("Cannot load json adapter '{}'", reference, throwable);
                continue;
            }

            // Finish
            adapters.add(adapter);
        }
    }

    // Object

    protected final @NotNull Map<Class<?>, Adapter<JsonElement, ?>> map = new HashMap<>();

    public JsonAdapters() {
        synchronized (lock) {
            for (@NotNull JsonAdapter<?> adapters : JsonAdapters.adapters) {
                map.put(adapters.getReference(), adapters);
            }
        }
    }

    @Override
    public <E> void put(@NotNull Adapter<JsonElement, E> adapter) {
        map.put(adapter.getReference(), adapter);
    }

    @Override
    public <E> boolean add(@NotNull Adapter<JsonElement, E> adapter) {
        if (map.containsKey(adapter.getReference())) {
            return false;
        }

        map.put(adapter.getReference(), adapter);
        return true;
    }

    @Override
    public <E> boolean remove(@NotNull Class<E> reference) {
        if (map.containsKey(reference)) {
            map.remove(reference);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean contains(@NotNull Class<?> reference) {
        @Nullable Adapter<JsonElement, ?> adapter = map.getOrDefault(reference, null);
        if (adapter != null) return true;

        // Check assignable
        for (@NotNull Class<?> r : map.keySet()) {
            if (r.isAssignableFrom(reference)) {
                return map.get(r) != null;
            }
        }

        return false;
    }

    @Override
    public boolean contains(@NotNull Adapter<JsonElement, ?> adapter) {
        return map.containsValue(adapter);
    }

    @Override
    public @NotNull <E> Optional<Adapter<JsonElement, E>> get(@NotNull Class<E> reference) {
        //noinspection unchecked
        @Nullable Adapter<JsonElement, E> adapter = (Adapter<JsonElement, E>) map.getOrDefault(reference, null);
        if (adapter != null) return Optional.of(adapter);

        // Check assignable
        for (@NotNull Class<?> r : map.keySet()) {
            if (r.isAssignableFrom(reference)) {
                //noinspection unchecked
                return Optional.ofNullable((Adapter<JsonElement, E>) map.get(r));
            }
        }

        return Optional.empty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public @NotNull Iterator<Adapter<JsonElement, ?>> iterator() {
        return map.values().iterator();
    }

}
