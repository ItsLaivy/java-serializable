package codes.laivy.serializable.context;

import codes.laivy.serializable.annotations.Concrete;
import org.jetbrains.annotations.NotNull;

@Concrete(type = ArrayContextImpl.class)
@Concrete(type = MapContextImpl.class)
@Concrete(type = NullContextImpl.class)
@Concrete(type = PrimitiveContextImpl.class)
public interface Context {

    // Object

    default @NotNull ArrayContext getAsArray() {
        if (this instanceof ArrayContext) {
            return (ArrayContext) this;
        } else {
            throw new IllegalStateException("this instance isn't an array context: " + this.getClass().getName());
        }
    }
    default boolean isArray() {
        return this instanceof ArrayContext;
    }

    default @NotNull PrimitiveContext getAsPrimitive() {
        if (this instanceof PrimitiveContext) {
            return (PrimitiveContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a primitive context: " + this.getClass().getName());
        }
    }
    default boolean isPrimitive() {
        return this instanceof PrimitiveContext;
    }

    default @NotNull MapContext getAsMap() {
        if (this instanceof MapContext) {
            return (MapContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a map context: " + this.getClass().getName());
        }
    }
    default boolean isMap() {
        return this instanceof MapContext;
    }

    default @NotNull NullContext getAsNull() {
        if (this instanceof NullContext) {
            return (NullContext) this;
        } else {
            throw new IllegalStateException("this instance isn't a null context: " + this.getClass().getName());
        }
    }
    default boolean isNull() {
        return this instanceof NullContext;
    }

}
