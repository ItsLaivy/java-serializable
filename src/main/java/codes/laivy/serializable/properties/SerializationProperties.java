package codes.laivy.serializable.properties;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.*;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static codes.laivy.serializable.utilities.Classes.getFields;

public interface SerializationProperties {

    // Static initializers

    static @NotNull SerializationProperties create(@NotNull Serializer serializer, @NotNull Field field, @NotNull Object instance) {
        @NotNull Class<?> reference = field.getType();
        @NotNull Father father = new FatherImpl(field, instance);
        boolean bypassTransients = field.isAnnotationPresent(BypassTransient.class);

        // Concretes
        @NotNull Set<Class<?>> typeConcretes = Arrays.stream(field.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toSet());
        @NotNull Set<Class<?>> genericConcretes = Arrays.stream(field.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toSet());

        // Instance factories
        @NotNull InstanceFactory instanceFactory = field.isAnnotationPresent(UseEmptyConstructor.class) ? InstanceFactory.constructor() : InstanceFactory.allocator();
        @NotNull ContextFactory contextFactory = field.isAnnotationPresent(UsingSerializers.class) ? ContextFactory.methods(reference, field.getAnnotation(UsingSerializers.class)) : ContextFactory.field();

        // Adapter
        @Nullable Adapter adapter = serializer.getAdapter(reference).orElse(null);

        // Fields
        @NotNull Set<Field> fields = new HashSet<>(getFields(father, reference).values());

        // @ExcludeFields and @OnlyFields annotation
        if (father.getField().isAnnotationPresent(OnlyFields.class)) {
            @NotNull Set<String> only = father.getField().isAnnotationPresent(OnlyFields.class) ? new HashSet<>(Arrays.asList(father.getField().getAnnotation(OnlyFields.class).fields())) : new HashSet<>();
            fields.removeIf(f -> !only.contains(f.getName()));
        } else if (father.getField().isAnnotationPresent(ExcludeFields.class)) {
            @NotNull Set<String> excluded = father.getField().isAnnotationPresent(ExcludeFields.class) ? new HashSet<>(Arrays.asList(father.getField().getAnnotation(ExcludeFields.class).fields())) : new HashSet<>();
            fields.removeIf(f -> excluded.contains(f.getName()));
        }

        // Finish
        return new SerializationPropertiesImpl(father, null, typeConcretes, genericConcretes, bypassTransients, fields, contextFactory, instanceFactory, adapter);
    }
    static @NotNull Builder builder() {
        return new Builder();
    }

    // Object

    @Nullable Father getFather();

    @Nullable Object getOuterInstance();

    @NotNull Collection<Class<?>> getTypeConcretes();
    @NotNull Collection<Class<?>> getGenericConcretes(@NotNull Type type);

    boolean isBypassTransients();
    void setBypassTransients(boolean bypass);

    @NotNull Collection<Field> getIncludedFields();

    @NotNull ContextFactory getContextFactory();
    @NotNull InstanceFactory getInstanceFactory();

    @Nullable Adapter getAdapter();
    void setAdapter(@Nullable Adapter adapter);

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
