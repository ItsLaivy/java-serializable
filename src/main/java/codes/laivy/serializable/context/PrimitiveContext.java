package codes.laivy.serializable.context;

import org.jetbrains.annotations.NotNull;

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
        if (getObject() instanceof String && (getAsString().equalsIgnoreCase("true") || getAsString().equalsIgnoreCase("false"))) {
            return getAsString().equalsIgnoreCase("true");
        } else if (getObject() instanceof Boolean) {
            return (boolean) getObject();
        } else {
            throw new IllegalStateException("this primitive context isn't a boolean!");
        }
    }
    default byte getAsByte() {
        return (byte) getObject();
    }
    default short getAsShort() {
        return (short) getObject();
    }
    default char getAsCharacter() {
        return getObject() instanceof Character ? (char) getObject() : getAsString().charAt(0);
    }
    default int getAsInteger() {
        return (int) getObject();
    }
    default long getAsLong() {
        return (long) getObject();
    }
    default float getAsFloat() {
        return (float) getObject();
    }
    default double getAsDouble() {
        return (double) getObject();
    }
    default @NotNull String getAsString() {
        return getObject() instanceof String ? (String) getObject() : String.valueOf(getObject());
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
