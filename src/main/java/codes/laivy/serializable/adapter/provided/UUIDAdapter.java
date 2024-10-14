package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.util.UUID;

public class UUIDAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] { UUID.class };
    }

    @Override
    public void serialize(@NotNull Object object, @NotNull SerializeOutputContext context) {
        context.write(((UUID) object).toString());
    }
    @Override
    public @NotNull Object deserialize(@NotNull SerializeInputContext context) throws EOFException {
        return UUID.fromString(context.readLine());
    }

}
