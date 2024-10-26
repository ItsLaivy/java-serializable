package codes.laivy.serializable.config;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.*;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static codes.laivy.serializable.utilities.Classes.getFields;
import static codes.laivy.serializable.utilities.Classes.isConcrete;

public interface Config {

    // Static initializers

    static @NotNull Config create() {
        return new ConfigImpl(null, null, new LinkedHashSet<>(), new HashMap<>(), false, new LinkedHashSet<>(), ContextFactory.field(), InstanceFactory.allocator(), null);
    }
    static @NotNull Config create(@NotNull Serializer serializer, @NotNull Class<?> reference) {
        // Concretes
        @NotNull Map<Type, Collection<Class<?>>> genericConcretes = new HashMap<>();

        // Instance factories
        @NotNull InstanceFactory instanceFactory;
        @NotNull ContextFactory contextFactory;

        if (reference.isAssignableFrom(UsingSerializers.class)) {
            contextFactory = ContextFactory.methods(reference, reference.getAnnotation(UsingSerializers.class));
        } else {
            contextFactory = ContextFactory.field();
        }

        if (reference.isAnnotationPresent(UseEmptyConstructor.class)) {
            instanceFactory = InstanceFactory.constructor();
        } else {
            instanceFactory = InstanceFactory.allocator();
        }

        // Adapter
        @Nullable Adapter adapter = serializer.getAdapter(reference).orElse(null);

        // Finish
        return new ConfigImpl(null, null, new LinkedHashSet<>(), genericConcretes, false, new LinkedHashSet<>(getFields(null, reference).values()), contextFactory, instanceFactory, adapter);
    }
    static @NotNull Config create(@NotNull Serializer serializer, @NotNull Father father) {
        @NotNull Field field = father.getField();
        @NotNull Class<?> reference = field.getType();
        boolean bypassTransients = field.isAnnotationPresent(BypassTransient.class);

        // Concretes
        @NotNull Set<Class<?>> typeConcretes = Arrays.stream(field.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toSet());
        @NotNull Map<Type, Collection<Class<?>>> genericConcretes = new HashMap<>();

        @NotNull LinkedList<AnnotatedElement> elements = new LinkedList<>();
        elements.add(field.getAnnotatedType());

        while (!elements.isEmpty()) {
            @NotNull AnnotatedElement element = elements.poll();

            if (element instanceof AnnotatedType) {
                @NotNull AnnotatedType annotated = (AnnotatedType) element;
                @NotNull Type type = annotated.getType();

                genericConcretes.putIfAbsent(type, new LinkedHashSet<>());

                if (type instanceof Class && isConcrete((Class<?>) type)) {
                    genericConcretes.get(type).add((Class<?>) type);
                }

                if (element instanceof AnnotatedParameterizedType) {
                    @NotNull AnnotatedParameterizedType parameterized = (AnnotatedParameterizedType) element;
                    elements.addAll(Arrays.asList(parameterized.getAnnotatedActualTypeArguments()));
                }
            }
        }

        // Context factory
        @NotNull ContextFactory contextFactory;

        // todo: The used reference should be the compatible!
        if (field.isAnnotationPresent(UsingSerializers.class)) {
            contextFactory = ContextFactory.methods(field.getDeclaringClass(), field.getDeclaringClass().getAnnotation(UsingSerializers.class));
        } else if (reference.isAnnotationPresent(UsingSerializers.class)) {
            contextFactory = ContextFactory.methods(reference, reference.getAnnotation(UsingSerializers.class));
        } else {
            contextFactory = ContextFactory.field();
        }

        // Instance factory
        @NotNull InstanceFactory instanceFactory;

        if (field.isAnnotationPresent(UseEmptyConstructor.class) || reference.isAnnotationPresent(UseEmptyConstructor.class)) {
            instanceFactory = InstanceFactory.constructor();
        } else {
            instanceFactory = InstanceFactory.allocator();
        }

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
        return new ConfigImpl(father, null, typeConcretes, genericConcretes, bypassTransients, fields, contextFactory, instanceFactory, adapter);
    }
    static @NotNull Builder builder() {
        return new Builder();
    }

    // Object
    
    @Nullable Father getFather();

    @Nullable Object getOuterInstance();
    void setOuterInstance(@Nullable Object instance);

    @NotNull Collection<Class<?>> getTypeConcretes();
    @NotNull Collection<Class<?>> getGenericConcretes(@NotNull Type type);

    boolean isBypassTransients();
    void setBypassTransients(boolean bypass);

    @NotNull Collection<Field> getIncludedFields();

    @NotNull ContextFactory getContextFactory();
    void setConextFactory(@NotNull ContextFactory contextFactory);

    @NotNull InstanceFactory getInstanceFactory();
    void setInstancFactory(@NotNull InstanceFactory instanceFactory);

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