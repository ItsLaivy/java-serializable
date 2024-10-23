package codes.laivy.serializable.context;

import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PrimitiveContextImpl implements PrimitiveContext {

    // Object

    public @NotNull Object object;
    private final @Nullable SerializationProperties properties;

    public PrimitiveContextImpl(@NotNull Object object, @Nullable SerializationProperties properties) {
        this.properties = properties;
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

    // Properties

    @Override
    public @Nullable SerializationProperties getProperties() {
        return properties;
    }

}
