package codes.laivy.serializable.config;

import codes.laivy.serializable.config.Config.Father;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

final class FatherImpl implements Father {

    // Object

    private final @NotNull Field field;
    private final @NotNull Object instance;

    public FatherImpl(@NotNull Field field, @NotNull Object instance) {
        this.field = field;
        this.instance = instance;
    }

    // Getters

    @Override
    public @NotNull Field getField() {
        return field;
    }
    @Override
    public @NotNull Object getInstance() {
        return instance;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof FatherImpl)) return false;
        @NotNull FatherImpl father = (FatherImpl) object;
        return Objects.equals(getField(), father.getField()) && Objects.equals(getInstance(), father.getInstance());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getField(), getInstance());
    }

    @Override
    public @NotNull String toString() {
        return "FatherImpl{" +
                "field=" + field +
                ", instance=" + instance +
                '}';
    }

}
