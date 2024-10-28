package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public class IllegalConcreteTypeException extends ClassCastException {
    public IllegalConcreteTypeException(@NotNull String s) {
        super(s);
    }
}
