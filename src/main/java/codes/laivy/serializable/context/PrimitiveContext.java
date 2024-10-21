package codes.laivy.serializable.context;

import org.jetbrains.annotations.NotNull;

public interface PrimitiveContext extends Context {

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
