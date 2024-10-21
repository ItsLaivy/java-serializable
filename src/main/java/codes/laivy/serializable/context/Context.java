package codes.laivy.serializable.context;

import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Context {

    @Nullable SerializationProperties getProperties();

    // Object

    default @NotNull ArrayContext getAsArrayContext() {
        if (this instanceof ArrayContext) {
            return (ArrayContext) this;
        } else {
            throw new IllegalStateException("this instance isn't an array context");
        }
    }
    default boolean isArrayContext() {
        return this instanceof ArrayContext;
    }

    default @NotNull PrimitiveContext getAsObjectContext() {
        if (this instanceof PrimitiveContext) {
            return (PrimitiveContext) this;
        } else {
            throw new IllegalStateException("this instance isn't an object context");
        }
    }
    default boolean isObjectContext() {
        return this instanceof PrimitiveContext;
    }

    default @NotNull MapContext getAsMapContext() {
        if (this instanceof MapContext) {
            return (MapContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a map context");
        }
    }
    default boolean isMapContext() {
        return this instanceof MapContext;
    }

    default @NotNull NullContext getAsNullContext() {
        if (this instanceof NullContext) {
            return (NullContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a null context");
        }
    }
    default boolean isNullContext() {
        return this instanceof NullContext;
    }

}
