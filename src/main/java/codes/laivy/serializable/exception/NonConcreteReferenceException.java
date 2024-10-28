package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public class NonConcreteReferenceException extends IllegalStateException {
    public NonConcreteReferenceException() {
    }
    public NonConcreteReferenceException(@NotNull String s) {
        super(s);
    }
}
