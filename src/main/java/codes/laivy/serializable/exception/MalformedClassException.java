package codes.laivy.serializable.exception;

import org.jetbrains.annotations.NotNull;

public class MalformedClassException extends RuntimeException {
    public MalformedClassException(@NotNull String message) {
        super(message);
    }
}
