package codes.laivy.serializable.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

final class Father {

    private final @NotNull Field field;
    private final @NotNull Object instance;

    public Father(@NotNull Field field, @NotNull Object instance) {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("cannot deserialize a static field");
        }

        this.field = field;
        this.instance = instance;
    }

    // Getters

    public @NotNull Field getField() {
        return field;
    }
    public @NotNull Object getInstance() {
        return instance;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Father)) return false;
        @NotNull Father father = (Father) object;
        return Objects.equals(getField(), father.getField()) && Objects.equals(getInstance(), father.getInstance());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getField(), getInstance());
    }

}
