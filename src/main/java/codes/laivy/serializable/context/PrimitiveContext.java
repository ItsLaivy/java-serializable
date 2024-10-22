package codes.laivy.serializable.context;

import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PrimitiveContext extends Context {

    // Static initializers

    static @NotNull PrimitiveContext create(@NotNull String string) {
        return new PrimitiveContextImpl(string, null);
    }
    static @NotNull PrimitiveContext create(@NotNull String string, @Nullable SerializationProperties properties) {
        return new PrimitiveContextImpl(string, properties);
    }

    static @NotNull PrimitiveContext create(@NotNull Number number) {
        return new PrimitiveContextImpl(number, null);
    }
    static @NotNull PrimitiveContext create(@NotNull Number number, @Nullable SerializationProperties properties) {
        return new PrimitiveContextImpl(number, properties);
    }

    static @NotNull PrimitiveContext create(@NotNull Character character) {
        return new PrimitiveContextImpl(character, null);
    }
    static @NotNull PrimitiveContext create(@NotNull Character character, @Nullable SerializationProperties properties) {
        return new PrimitiveContextImpl(character, properties);
    }

    static @NotNull PrimitiveContext create(@NotNull Boolean bool) {
        return new PrimitiveContextImpl(bool, null);
    }
    static @NotNull PrimitiveContext create(@NotNull Boolean bool, @Nullable SerializationProperties properties) {
        return new PrimitiveContextImpl(bool, properties);
    }

    // Object

    boolean getAsBoolean();
    byte getAsByte();
    short getAsShort();
    char getAsCharacter();
    int getAsInteger();
    long getAsLong();
    float getAsFloat();
    double getAsDouble();
    @NotNull String getAsString();
    @NotNull Number getAsNumber();

    void setAsBoolean(boolean b);
    void setAsByte(byte b);
    void setAsShort(short s);
    void setAsCharacter(char c);
    void setAsInteger(int i);
    void setAsLong(long l);
    void setAsFloat(float f);
    void setAsDouble(double d);
    void setAsString(@NotNull String string);

    boolean isNumber();
    boolean isCharacter();
    boolean isString();
    boolean isBoolean();

}
