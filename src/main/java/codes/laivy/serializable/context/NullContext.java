package codes.laivy.serializable.context;

import org.jetbrains.annotations.NotNull;

public interface NullContext extends Context {

    static @NotNull NullContext create() {
        return new NullContextImpl();
    }

}
