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

    // Object

}
