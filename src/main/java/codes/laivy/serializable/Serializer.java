package codes.laivy.serializable;

import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public interface Serializer {

    // Static initializers

    static @NotNull JsonElement toJson(@NotNull Object object) {
        if (object instanceof Enum<?>[]) {
            return JsonSerializer.getInstance().serialize((Enum<?>[]) object);
        } else if (object instanceof Enum<?>) {
            return JsonSerializer.getInstance().serialize((Enum<?>) object);
        } else if (object instanceof Boolean[]) {
            return JsonSerializer.getInstance().serialize((Boolean[]) object);
        } else if (object instanceof Boolean) {
            return JsonSerializer.getInstance().serialize((Boolean) object);
        } else if (object instanceof Short[]) {
            return JsonSerializer.getInstance().serialize((Short[]) object);
        } else if (object instanceof Short) {
            return JsonSerializer.getInstance().serialize((Short) object);
        } else if (object instanceof Integer[]) {
            return JsonSerializer.getInstance().serialize((Integer[]) object);
        } else if (object instanceof Integer) {
            return JsonSerializer.getInstance().serialize((Integer) object);
        } else if (object instanceof Long[]) {
            return JsonSerializer.getInstance().serialize((Long[]) object);
        } else if (object instanceof Long) {
            return JsonSerializer.getInstance().serialize((Long) object);
        } else if (object instanceof Float[]) {
            return JsonSerializer.getInstance().serialize((Float[]) object);
        } else if (object instanceof Float) {
            return JsonSerializer.getInstance().serialize((Float) object);
        } else if (object instanceof Double[]) {
            return JsonSerializer.getInstance().serialize((Double[]) object);
        } else if (object instanceof Double) {
            return JsonSerializer.getInstance().serialize((Double) object);
        } else if (object instanceof Character[]) {
            return JsonSerializer.getInstance().serialize((Character[]) object);
        } else if (object instanceof Character) {
            return JsonSerializer.getInstance().serialize((Character) object);
        } else if (object instanceof Byte[]) {
            return JsonSerializer.getInstance().serialize((Byte[]) object);
        } else if (object instanceof Byte) {
            return JsonSerializer.getInstance().serialize((Byte) object);
        } else if (object instanceof String[]) {
            return JsonSerializer.getInstance().serialize((String[]) object);
        } else if (object instanceof String) {
            return JsonSerializer.getInstance().serialize((String) object);
        } else if (object instanceof Serializable[]) {
            return JsonSerializer.getInstance().serialize((Serializable[]) object);
        } else if (object instanceof Serializable) {
            return JsonSerializer.getInstance().serialize((Serializable) object);
        } else {
            return JsonSerializer.getInstance().serialize(object);
        }
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

    // Object

}
