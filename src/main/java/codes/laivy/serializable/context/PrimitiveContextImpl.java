package codes.laivy.serializable.context;

import codes.laivy.serializable.properties.SerializationProperties;
import com.google.gson.internal.LazilyParsedNumber;
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
    public boolean getAsBoolean() {
        if (isString() && (getAsString().equalsIgnoreCase("true") || getAsString().equalsIgnoreCase("false"))) {
            return getAsString().equalsIgnoreCase("true");
        } else if (isBoolean()) {
            return (boolean) object;
        } else {
            throw new IllegalStateException("this primitive context isn't a boolean!");
        }
    }
    @Override
    public byte getAsByte() {
        return getAsNumber().byteValue();
    }
    @Override
    public short getAsShort() {
        return getAsNumber().shortValue();
    }
    @Override
    public char getAsCharacter() {
        return getAsString().charAt(0);
    }
    @Override
    public int getAsInteger() {
        return getAsNumber().intValue();
    }
    @Override
    public long getAsLong() {
        return getAsNumber().longValue();
    }
    @Override
    public float getAsFloat() {
        return getAsNumber().floatValue();
    }
    @Override
    public double getAsDouble() {
        return getAsNumber().doubleValue();
    }
    @Override
    public @NotNull String getAsString() {
        return isString() ? (String) object : String.valueOf(object);
    }
    @Override
    public @NotNull Number getAsNumber() {
        if (isNumber()) {
            return (Number) object;
        } else if (isString()) {
            return new LazilyParsedNumber(getAsString());
        } else {
            throw new IllegalStateException("this primitive context isn't a number!");
        }
    }

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
    public boolean isNumber() {
        return object instanceof Number;
    }
    @Override
    public boolean isCharacter() {
        return object instanceof Character;
    }
    @Override
    public boolean isString() {
        return object instanceof String;
    }
    @Override
    public boolean isBoolean() {
        return object instanceof Boolean;
    }

    // Properties

    @Override
    public @Nullable SerializationProperties getProperties() {
        return properties;
    }

}
