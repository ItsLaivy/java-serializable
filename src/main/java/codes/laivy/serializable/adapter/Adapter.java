package codes.laivy.serializable.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;

public interface Adapter<T, O> {

    // Getters

    @NotNull Class<O> getReference();

    // Objects pure serialization

    @Nullable T serialize(@Nullable O object) throws InvalidClassException;
    @Nullable O deserialize(@NotNull Class<O> reference, @Nullable T object) throws InvalidClassException;

}
