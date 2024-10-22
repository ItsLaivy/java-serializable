package codes.laivy.serializable.adapter;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;

public interface Adapter {

    @NotNull Class<?> @NotNull [] getReferences();

    @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties);
    @NotNull Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException;

}
