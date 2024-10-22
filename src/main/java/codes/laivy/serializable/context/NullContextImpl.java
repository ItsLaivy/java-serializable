package codes.laivy.serializable.context;

import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final class NullContextImpl implements NullContext {

    // Object

    private final @Nullable SerializationProperties properties;

    public NullContextImpl(@Nullable SerializationProperties properties) {
        this.properties = properties;
    }

    // Getters

    @Override
    public @Nullable SerializationProperties getProperties() {
        return properties;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof NullContextImpl)) return false;
        @NotNull NullContextImpl that = (NullContextImpl) object;
        return Objects.equals(getProperties(), that.getProperties());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getProperties());
    }

    @Override
    public @NotNull String toString() {
        return "NullContextImpl{" +
                "properties=" + properties +
                '}';
    }

}
