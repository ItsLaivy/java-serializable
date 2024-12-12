package codes.laivy.serializable.context;

import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@MethodSerialization
final class PrimitiveContextImpl implements PrimitiveContext {

    // Object

    public @NotNull Object object;

    public PrimitiveContextImpl(@NotNull Object object) {
        this.object = object;
    }

    // Modules

    @Override
    public void setAsBoolean(boolean b) {
        object = b;
    }
    @Override
    public void setAsByte(byte b) {
        object = b;
    }
    @Override
    public void setAsShort(short s) {
        object = s;
    }
    @Override
    public void setAsCharacter(char c) {
        object = c;
    }
    @Override
    public void setAsInteger(int i) {
        object = i;
    }
    @Override
    public void setAsLong(long l) {
        object = l;
    }
    @Override
    public void setAsFloat(float f) {
        object = f;
    }
    @Override
    public void setAsDouble(double d) {
        object = d;
    }
    @Override
    public void setAsString(@NotNull String string) {
        object = string;
    }

    @Override
    public @NotNull Object getObject() {
        return object;
    }

    // Serializers

    private static @NotNull Object serialize(@NotNull PrimitiveContextImpl context) {
        return context.getObject();
    }
    private static @NotNull PrimitiveContextImpl deserialize(@NotNull Object object) {
        return new PrimitiveContextImpl(object);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof PrimitiveContextImpl)) return false;
        @NotNull PrimitiveContextImpl that = (PrimitiveContextImpl) object;
        return Objects.equals(getObject(), that.getObject());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getObject());
    }

    @Override
    public @NotNull String toString() {
        return String.valueOf(object);
    }

}
