package codes.laivy.serializable.context;

import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NullContext extends Context {

    static @NotNull NullContext create() {
        return create(null);
    }
    static @NotNull NullContext create(@Nullable SerializationProperties properties) {
        return new NullContextImpl(properties);
    }

}
