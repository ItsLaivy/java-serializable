package codes.laivy.serializable.context;

import org.jetbrains.annotations.NotNull;

public interface Context {

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

    default @NotNull PrimitiveContext getAsPrimitiveContext() {
        if (this instanceof PrimitiveContext) {
            return (PrimitiveContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a primitive context");
        }
    }
    default boolean isPrimitiveContext() {
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
