package codes.laivy.serializable;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.json.JsonSerializer;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
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

    // Serializable

    @Nullable Object serialize(@Nullable Serializable object) throws MalformedClassException;
    @Nullable Object serialize(@Nullable Serializable object, @Nullable SerializationProperties properties) throws MalformedClassException;

    @NotNull Iterable<?> serialize(@Nullable Serializable @NotNull ... array);
    @NotNull Iterable<?> serialize(@NotNull Iterable<@Nullable Serializable> iterable);
    @NotNull Iterable<?> serialize(@Nullable Object @NotNull ... array);

    // Enum

    @Nullable Object serialize(@Nullable Enum<?> e, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Enum<?> e);
    @NotNull Iterable<?> serialize(@Nullable Enum<?> @NotNull ... array);

    // Boolean

    @Nullable Object serialize(@Nullable Boolean b, @Nullable SerializationProperties properties);
    @NotNull Object serialize(boolean b, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Boolean b);
    @NotNull Object serialize(boolean b);

    @NotNull Iterable<?> serialize(@Nullable Boolean @NotNull ... array);
    @NotNull Iterable<?> serialize(boolean @NotNull ... array);

    // Short

    @Nullable Object serialize(@Nullable Short s, @Nullable SerializationProperties properties);
    @NotNull Object serialize(short s, @Nullable SerializationProperties properties);

    @NotNull Object serialize(short s);
    @Nullable Object serialize(@Nullable Short s);

    @NotNull Iterable<?> serialize(@Nullable Short @NotNull ... array);
    @NotNull Iterable<?> serialize(short @NotNull ... array);

    // Integer

    @Nullable Object serialize(@Nullable Integer i, @Nullable SerializationProperties properties);
    @NotNull Object serialize(int i, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Integer i);
    @NotNull Object serialize(int i);

    @NotNull Iterable<?> serialize(@Nullable Integer @NotNull ... array);
    @NotNull Iterable<?> serialize(int @NotNull ... array);

    // Long

    @Nullable Object serialize(@Nullable Long l, @Nullable SerializationProperties properties);
    @NotNull Object serialize(long l, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Long l);
    @NotNull Object serialize(long l);

    @NotNull Iterable<?> serialize(@Nullable Long @NotNull ... array);
    @NotNull Iterable<?> serialize(long @NotNull ... array);

    // Float

    @Nullable Object serialize(@Nullable Float f, @Nullable SerializationProperties properties);
    @NotNull Object serialize(float f, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Float f);
    @NotNull Object serialize(float f);

    @NotNull Iterable<?> serialize(@Nullable Float @NotNull ... array);
    @NotNull Iterable<?> serialize(float @NotNull ... array);

    // Double

    @Nullable Object serialize(@Nullable Double d, @Nullable SerializationProperties properties);
    @NotNull Object serialize(double d, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Double d);
    @NotNull Object serialize(double d);

    @NotNull Iterable<?> serialize(@Nullable Double @NotNull ... array);
    @NotNull Iterable<?> serialize(double @NotNull ... array);

    // Character

    @Nullable Object serialize(@Nullable Character c, @Nullable SerializationProperties properties);
    @NotNull Object serialize(char c, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Character c);
    @NotNull Object serialize(char c);

    @NotNull Iterable<?> serialize(@Nullable Character @NotNull ... array);
    @NotNull Iterable<?> serialize(char @NotNull ... array);

    // Byte

    @Nullable Object serialize(@Nullable Byte b, @Nullable SerializationProperties properties);
    @NotNull Object serialize(byte b, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable Byte b);
    @NotNull Object serialize(byte b);

    @NotNull Iterable<?> serialize(@Nullable Byte @NotNull ... array);
    @NotNull Iterable<?> serialize(byte @NotNull ... array);

    // String

    @Nullable Object serialize(@Nullable String string, @Nullable SerializationProperties properties);

    @Nullable Object serialize(@Nullable String string);
    @NotNull Iterable<?> serialize(@Nullable String @NotNull ... array);

    // Pure

    @Nullable Object serialize(@Nullable Object object) throws MalformedClassException;
    @Nullable Object serialize(@Nullable Object object, @Nullable SerializationProperties properties);

    // Context

    <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context);
    <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context, @Nullable SerializationProperties properties);

    @Nullable Object deserialize(@NotNull References references, @NotNull Context context);
    @Nullable Object deserialize(@NotNull References references, @NotNull Context context, @Nullable SerializationProperties properties);

    @NotNull Context toContext(@Nullable Object object);
    @NotNull Context toContext(@Nullable Object object, @Nullable SerializationProperties properties);

}
