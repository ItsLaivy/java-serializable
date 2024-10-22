package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.PrimitiveContext;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.UUID;

public class UUIDAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] { UUID.class };
    }

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        return PrimitiveContext.create(object.toString(), properties);
    }
    @Override
    public @NotNull Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException {
        return UUID.fromString(context.getAsObjectContext().getAsString());
    }

}
