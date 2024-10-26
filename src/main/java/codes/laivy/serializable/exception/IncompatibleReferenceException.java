package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public class IncompatibleReferenceException extends IllegalStateException {
    public IncompatibleReferenceException(@NotNull String s) {
        super(s);
    }
    public IncompatibleReferenceException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
