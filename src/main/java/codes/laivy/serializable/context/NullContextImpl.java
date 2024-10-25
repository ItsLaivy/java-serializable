package codes.laivy.serializable.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NullContextImpl implements NullContext {

    // Object

    public NullContextImpl() {
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
