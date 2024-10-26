package codes.laivy.serializable.context;

import org.jetbrains.annotations.NotNull;

public interface Context {

    // Object

    default @NotNull ArrayContext getAsArray() {
        if (this instanceof ArrayContext) {
            return (ArrayContext) this;
        } else {
            throw new IllegalStateException("this instance isn't an array context");
        }
    }
    default boolean isArray() {
        return this instanceof ArrayContext;
    }

    default @NotNull PrimitiveContext getAsPrimitive() {
        if (this instanceof PrimitiveContext) {
            return (PrimitiveContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a primitive context");
        }
    }
    default boolean isPrimitive() {
        return this instanceof PrimitiveContext;
    }

    default @NotNull MapContext getAsMap() {
        if (this instanceof MapContext) {
            return (MapContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a map context");
        }
    }
    default boolean isMap() {
        return this instanceof MapContext;
    }

    default @NotNull NullContext getAsNull() {
        if (this instanceof NullContext) {
            return (NullContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a null context");
        }
    }
    default boolean isNull() {
        return this instanceof NullContext;
    }

}
