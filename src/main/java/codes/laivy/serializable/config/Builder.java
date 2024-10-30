package codes.laivy.serializable.config;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config.Father;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public final class Builder {

    // Object

    private @Nullable Father father;
    private @Nullable Object outerInstance;

    private final @NotNull Set<Class<?>> typeConcretes = new LinkedHashSet<>();
    private final @NotNull Map<Type, Collection<Class<?>>> genericConcretes = new LinkedHashMap<>();

    private boolean bypassTransients = false;

    private final @NotNull Set<Field> includedFields = new LinkedHashSet<>();

    private @NotNull ContextFactory contextFactory = ContextFactory.field();
    private @NotNull InstanceFactory instanceFactory = InstanceFactory.allocator();

    private @Nullable Adapter adapter;

    Builder() {
    }

    // Modules

    @Contract(value = "_->this")
    public @NotNull Builder father(@NotNull Father father) {
        this.father = father;
        return this;
    }
    public @Nullable Father father() {
        return father;
    }

    @Contract(value = "_->this")
    public @NotNull Builder outerInstance(@NotNull Object outerInstance) {
        this.outerInstance = outerInstance;
        return this;
    }
    public @Nullable Object outerInstance() {
        return outerInstance;
    }

    @Contract(value = "_->this")
    public @NotNull Builder addGenericConcrete(@NotNull Class<?> reference) {
        typeConcretes.add(reference);
        return this;
    }
    @Contract(value = "_,_->this")
    public @NotNull Builder addTypeConcrete(@NotNull Type type, @NotNull Class<?> reference) {
        genericConcretes.putIfAbsent(type, new LinkedList<>());
        genericConcretes.get(type).add(reference);

        return this;
    }

    @Contract(value = "_->this")
    public @NotNull Builder bypassTransients(boolean bypassTransients) {
        this.bypassTransients = bypassTransients;
        return this;
    }

    @Contract(value = "_->this")
    public @NotNull Builder addIncludedFields(@NotNull Field field) {
        includedFields.add(field);
        return this;
    }

    @Contract(value = "_->this")
    public @NotNull Builder instanceFactory(@NotNull InstanceFactory instanceFactory) {
        this.instanceFactory = instanceFactory;
        return this;
    }
    @Contract(value = "_->this")
    public @NotNull Builder contextFactory(@NotNull ContextFactory contextFactory) {
        this.contextFactory = contextFactory;
        return this;
    }

    @Contract(value = "_->this")
    public @NotNull Builder adapter(@NotNull Adapter adapter) {
        this.adapter = adapter;
        return this;
    }

    // Builder

    public @NotNull Config build() {
        return new ConfigImpl(father, outerInstance, typeConcretes, genericConcretes, bypassTransients, includedFields, contextFactory, instanceFactory, adapter);
    }

}
