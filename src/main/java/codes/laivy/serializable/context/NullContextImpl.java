package codes.laivy.serializable.context;

import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MethodSerialization
final class NullContextImpl implements NullContext {

    // Object

    public NullContextImpl() {
    }

    // Serializers

    private static @Nullable Object serialize(@NotNull NullContextImpl context) {
        return null;
    }
    private static @NotNull NullContextImpl deserialize(@Nullable Object object) {
        return new NullContextImpl();
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof NullContextImpl)) return false;
        return true;
    }
    @Override
    public int hashCode() {
        return -30;
    }

    @Override
    public @NotNull String toString() {
        return "null";
    }

}
