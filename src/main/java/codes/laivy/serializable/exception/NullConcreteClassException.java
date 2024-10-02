package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public final class NullConcreteClassException extends NullPointerException {
    public NullConcreteClassException(@NotNull String s) {
        super(s);
    }
}
