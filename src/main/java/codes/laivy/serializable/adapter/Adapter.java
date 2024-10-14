package codes.laivy.serializable.adapter;

import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

public interface Adapter {

    @NotNull Class<?> @NotNull [] getReferences();

    void serialize(@NotNull Object object, @NotNull SerializeOutputContext context);
    @NotNull Object deserialize(@NotNull SerializeInputContext context) throws EOFException;

}
