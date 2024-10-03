package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public final class MalformedSerializerException extends IllegalStateException {
    public MalformedSerializerException(@NotNull String s) {
        super(s);
    }
}
