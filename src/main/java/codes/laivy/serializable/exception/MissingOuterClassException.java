package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public final class MissingOuterClassException extends IllegalStateException {
    public MissingOuterClassException(@NotNull String s) {
        super(s);
    }
}
