package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.PrimitiveContext;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.util.UUID;

public class UUIDAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] { UUID.class };
    }

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @NotNull Config config) {
        return PrimitiveContext.create(object.toString());
    }
    @Override
    public @NotNull Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws EOFException {
        return UUID.fromString(context.getAsPrimitive().getAsString());
    }

}
