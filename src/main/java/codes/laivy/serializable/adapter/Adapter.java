package codes.laivy.serializable.adapter;

import codes.laivy.serializable.factory.context.ContextFactory;
import org.jetbrains.annotations.NotNull;

public interface Adapter extends ContextFactory {
    @NotNull Class<?> @NotNull [] getReferences();
}
