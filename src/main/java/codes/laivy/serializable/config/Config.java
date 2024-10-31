package codes.laivy.serializable.config;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

public interface Config {

    // Static initializers

    static @NotNull Builder builder() {
        return new Builder();
    }
    static @NotNull Builder builder(@NotNull Serializer serializer, @NotNull Class<?> reference) {
        return new Builder(serializer, reference);
    }
    static @NotNull Builder builder(@NotNull Serializer serializer, @NotNull Class<?> reference, @NotNull Father father) {
        return new Builder(serializer, reference, father);
    }

    // Object
    
    @Nullable Father getFather();

    @Nullable Object getOuterInstance();
    void setOuterInstance(@Nullable Object instance);

    @NotNull Collection<Class<?>> getTypes();

    @NotNull Collection<Class<?>> getGenerics();
    @NotNull Collection<Class<?>> getGenerics(@NotNull Type type);

    boolean isBypassTransients();
    void setBypassTransients(boolean bypass);

    @NotNull Collection<Field> getIncludedFields();

    @NotNull ContextFactory getContextFactory();
    void setConextFactory(@NotNull ContextFactory contextFactory);

    @NotNull InstanceFactory getInstanceFactory();
    void setInstancFactory(@NotNull InstanceFactory instanceFactory);

    @Nullable Adapter getAdapter();
    void setAdapter(@Nullable Adapter adapter);

    /**
     * If true, this method will ignore the casting at the #readResolve and #writeReplace methods,
     * allowing those methods to return types that isn't originally assignable from the original declaring class/reference
     * @return
     */
    boolean isIgnoreCasting();
    void setIgnoreCasting(boolean ignoreCasting);

    // Classes

    interface Father {

        // Static initializers

        static @NotNull Father create(@NotNull Field field, @NotNull Object instance) {
            return new FatherImpl(field, instance);
        }

        // Object

        @NotNull Field getField();
        @NotNull Object getInstance();

    }

}
