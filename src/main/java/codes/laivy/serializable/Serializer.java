package codes.laivy.serializable;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

// todo #getAllAdapters static method
public interface Serializer {

    // Static initializers

    static @NotNull JsonElement toJson(@Nullable Object object) {
        return Objects.requireNonNull(JsonSerializer.getInstance().serialize(object));
    }
    static @NotNull JsonElement toJson(@Nullable Object object, @NotNull Config config) {
        return Objects.requireNonNull(JsonSerializer.getInstance().serialize(object, config));
    }

    static <T> @Nullable T fromJson(@NotNull Class<T> reference, @Nullable JsonElement element) {
        return JsonSerializer.getInstance().deserialize(reference, element);
    }
    static <T> @Nullable T fromJson(@NotNull Class<T> reference, @Nullable JsonElement element, @NotNull Config config) {
        return JsonSerializer.getInstance().deserialize(reference, element, config);
    }

    static <T> @NotNull Iterable<@Nullable T> fromJson(@NotNull Class<T> reference, @Nullable JsonElement @NotNull ... array) {
        return JsonSerializer.getInstance().deserialize(reference, array);
    }
    static <T> @NotNull Iterable<@Nullable T> fromJson(@NotNull Class<T> reference, @NotNull JsonArray array, @NotNull Config config) {
        return JsonSerializer.getInstance().deserialize(reference, (Iterable<JsonElement>) array, config);
    }

    // Adapters

    @NotNull Collection<Adapter> getAdapters();
    @NotNull Optional<Adapter> getAdapter(@NotNull Class<?> reference);

    // Serializable

    @Nullable Object serialize(@Nullable Serializable object, @NotNull Config config);
    @Nullable Object serialize(@Nullable Serializable object);

    @NotNull Iterable<?> serialize(@Nullable Serializable @NotNull ... array);
    @NotNull Iterable<?> serialize(@Nullable Object @NotNull ... array);

    @NotNull Iterable<?> serialize(@NotNull Iterable<@Nullable Serializable> iterable);
    @NotNull Iterable<?> serialize(@NotNull Iterable<@Nullable Serializable> iterable, Config config);

    // Enum

    @Nullable Object serialize(@Nullable Enum<?> e, @NotNull Config config);

    @Nullable Object serialize(@Nullable Enum<?> e);
    @NotNull Iterable<?> serialize(@Nullable Enum<?> @NotNull ... array);

    // Boolean

    @Nullable Object serialize(@Nullable Boolean b, @NotNull Config config);
    @NotNull Object serialize(boolean b, @NotNull Config config);

    @Nullable Object serialize(@Nullable Boolean b);
    @NotNull Object serialize(boolean b);

    @NotNull Iterable<?> serialize(@Nullable Boolean @NotNull ... array);
    @NotNull Iterable<?> serialize(boolean @NotNull ... array);

    // Short

    @Nullable Object serialize(@Nullable Short s, @NotNull Config config);
    @NotNull Object serialize(short s, @NotNull Config config);

    @NotNull Object serialize(short s);
    @Nullable Object serialize(@Nullable Short s);

    @NotNull Iterable<?> serialize(@Nullable Short @NotNull ... array);
    @NotNull Iterable<?> serialize(short @NotNull ... array);

    // Integer

    @Nullable Object serialize(@Nullable Integer i, @NotNull Config config);
    @NotNull Object serialize(int i, @NotNull Config config);

    @Nullable Object serialize(@Nullable Integer i);
    @NotNull Object serialize(int i);

    @NotNull Iterable<?> serialize(@Nullable Integer @NotNull ... array);
    @NotNull Iterable<?> serialize(int @NotNull ... array);

    // Long

    @Nullable Object serialize(@Nullable Long l, @NotNull Config config);
    @NotNull Object serialize(long l, @NotNull Config config);

    @Nullable Object serialize(@Nullable Long l);
    @NotNull Object serialize(long l);

    @NotNull Iterable<?> serialize(@Nullable Long @NotNull ... array);
    @NotNull Iterable<?> serialize(long @NotNull ... array);

    // Float

    @Nullable Object serialize(@Nullable Float f, @NotNull Config config);
    @NotNull Object serialize(float f, @NotNull Config config);

    @Nullable Object serialize(@Nullable Float f);
    @NotNull Object serialize(float f);

    @NotNull Iterable<?> serialize(@Nullable Float @NotNull ... array);
    @NotNull Iterable<?> serialize(float @NotNull ... array);

    // Double

    @Nullable Object serialize(@Nullable Double d, @NotNull Config config);
    @NotNull Object serialize(double d, @NotNull Config config);

    @Nullable Object serialize(@Nullable Double d);
    @NotNull Object serialize(double d);

    @NotNull Iterable<?> serialize(@Nullable Double @NotNull ... array);
    @NotNull Iterable<?> serialize(double @NotNull ... array);

    // Character

    @Nullable Object serialize(@Nullable Character c, @NotNull Config config);
    @NotNull Object serialize(char c, @NotNull Config config);

    @Nullable Object serialize(@Nullable Character c);
    @NotNull Object serialize(char c);

    @NotNull Iterable<?> serialize(@Nullable Character @NotNull ... array);
    @NotNull Iterable<?> serialize(char @NotNull ... array);

    // Byte

    @Nullable Object serialize(@Nullable Byte b, @NotNull Config config);
    @NotNull Object serialize(byte b, @NotNull Config config);

    @Nullable Object serialize(@Nullable Byte b);
    @NotNull Object serialize(byte b);

    @NotNull Iterable<?> serialize(@Nullable Byte @NotNull ... array);
    @NotNull Iterable<?> serialize(byte @NotNull ... array);

    // String

    @Nullable Object serialize(@Nullable String string, @NotNull Config config);

    @Nullable Object serialize(@Nullable String string);
    @NotNull Iterable<?> serialize(@Nullable String @NotNull ... array);

    // Pure

    @Nullable Object serialize(@Nullable Object object);
    @Nullable Object serialize(@Nullable Object object, @NotNull Config config);

    // Context

    <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context) throws IncompatibleReferenceException;
    <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context, @NotNull Config config) throws IncompatibleReferenceException;

    @NotNull Context toContext(@Nullable Object object);
    @NotNull Context toContext(@Nullable Object object, @NotNull Config config);

}
