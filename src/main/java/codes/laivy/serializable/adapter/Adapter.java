package codes.laivy.serializable.adapter;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

public interface Adapter {

    @NotNull Class<?> @NotNull [] getReferences();

    @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @NotNull Config config);
    @NotNull Object read(@NotNull Class<?> reference, @NotNull Context context, @NotNull Config config) throws EOFException;

}
