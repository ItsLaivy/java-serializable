package codes.laivy.serializable;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.json.JsonSerializer;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface Serializer {

    // Static initializers

    static @NotNull JsonElement toJson(@NotNull Object object) {
        return JsonSerializer.getInstance().serialize(object);
    }

    static <T> @Nullable T fromJson(@NotNull Class<T> reference, @Nullable JsonElement element) {
        return JsonSerializer.getInstance().deserialize(reference, element);
    }
    static <T> @NotNull Iterable<@Nullable T> fromJson(@NotNull Class<T> reference, @Nullable JsonElement @NotNull ... array) {
        return JsonSerializer.getInstance().deserialize(reference, array);
    }
    static <T> @NotNull Iterable<@Nullable T> fromJson(@NotNull Class<T> reference, @NotNull JsonArray array) {
        return JsonSerializer.getInstance().deserialize(reference, array);
    }

    // Adapters

    @NotNull Collection<Adapter> getAdapters();
    @NotNull Optional<Adapter> getAdapter(@NotNull Class<?> reference);

    // Context

    default <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context) {
        return deserialize(reference, context, null);
    }
    default <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context, @Nullable SerializationProperties properties) {
        @Nullable Object object = deserialize(References.of(reference), context, properties);

        if (object != null && !reference.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("unexpected object reference '" + object.getClass().getName() + "', expected '" + reference.getName() + "': " + object);
        }

        //noinspection unchecked
        return (E) object;
    }

    default @Nullable Object deserialize(@NotNull References references, @NotNull Context context) {
        return deserialize(references, context, null);
    }
    @Nullable Object deserialize(@NotNull References references, @NotNull Context context, @Nullable SerializationProperties properties);

    default @NotNull Context toContext(@Nullable Object object) {
        return toContext(object, null);
    }
    @NotNull Context toContext(@Nullable Object object, @Nullable SerializationProperties properties);

}
