package codes.laivy.serializable.context;

import codes.laivy.serializable.annotations.Concrete;
import org.jetbrains.annotations.NotNull;

@Concrete(type = PrimitiveContextImpl.class)
public interface PrimitiveContext extends Context {

    // Static initializers

    static @NotNull PrimitiveContext create(@NotNull String string) {
        return new PrimitiveContextImpl(string);
    }
    static @NotNull PrimitiveContext create(@NotNull Number number) {
        return new PrimitiveContextImpl(number);
    }
    static @NotNull PrimitiveContext create(@NotNull Character character) {
        return new PrimitiveContextImpl(character);
    }
    static @NotNull PrimitiveContext create(@NotNull Boolean bool) {
        return new PrimitiveContextImpl(bool);
    }

    // Object

    default boolean getAsBoolean() {
        if (getObject() instanceof Boolean) {
            return (boolean) getObject();
        } else if (getObject() instanceof String && (getAsString().equalsIgnoreCase("true") || getAsString().equalsIgnoreCase("false"))) {
            return getAsString().equalsIgnoreCase("true");
        } else {
            throw new IllegalStateException("this primitive context isn't a boolean: " + getObject());
        }
    }
    default byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }
    default short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
    }
    default char getAsCharacter() {
        return getObject() instanceof Character ? (char) getObject() : getAsString().charAt(0);
    }
    default int getAsInteger() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }
    default long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }
    default float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }
    default double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }
    default @NotNull String getAsString() {
        return getObject() instanceof String ? (String) getObject() : String.valueOf(getObject());
    }
    default @NotNull Number getAsNumber() {
        return (Number) getObject();
    }

    void setAsBoolean(boolean b);
    void setAsByte(byte b);
    void setAsShort(short s);
    void setAsCharacter(char c);
    void setAsInteger(int i);
    void setAsLong(long l);
    void setAsFloat(float f);
    void setAsDouble(double d);
    void setAsString(@NotNull String string);

    @NotNull Object getObject();

    default boolean isNumber() {
        return getObject() instanceof Number;
    }
    default boolean isBoolean() {
        return getObject() instanceof Boolean;
    }
    default boolean isString() {
        return getObject() instanceof String;
    }

}
