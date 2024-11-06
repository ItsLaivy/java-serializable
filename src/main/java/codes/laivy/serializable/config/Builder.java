package codes.laivy.serializable.config;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.*;
import codes.laivy.serializable.config.Config.Father;
import codes.laivy.serializable.exception.IllegalConcreteTypeException;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static codes.laivy.serializable.utilities.Classes.getFields;
import static codes.laivy.serializable.utilities.Classes.isConcrete;

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

    private boolean ignoreCasting = true;

    Builder() {
    }
    Builder(@NotNull Serializer serializer, @NotNull Class<?> reference) {
        @NotNull Set<Class<?>> references = Classes.getReferences(reference);

        if (references.size() == 1 && !isConcrete(reference)) {
            throw new IllegalConcreteTypeException("this reference '" + reference + "' isn't concrete. Try to include @Concrete annotations");
        }

        // Factories
        @NotNull InstanceFactory instanceFactory;
        @NotNull ContextFactory contextFactory;

        if (reference.isAnnotationPresent(UsingSerializers.class)) {
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
        this.father = null;
        this.outerInstance = null;
        this.typeConcretes.addAll(references);
        this.bypassTransients = false;
        this.includedFields.addAll(getFields(null, reference).values());
        this.contextFactory = contextFactory;
        this.instanceFactory = instanceFactory;
        this.adapter = adapter;
        this.ignoreCasting = true;
    }
    Builder(@NotNull Serializer serializer, @NotNull Class<?> reference, @NotNull Father father) {
        @NotNull Field field = father.getField();
        boolean bypassTransients = field.isAnnotationPresent(BypassTransient.class);

        // Concretes
        @NotNull Set<Class<?>> typeConcretes = new LinkedHashSet<>();
        typeConcretes.add(reference);
        typeConcretes.addAll(Classes.getReferences(father.getField()));

        // Generics
        @NotNull Map<Type, Collection<Class<?>>> genericConcretes = new LinkedHashMap<>();

        @NotNull LinkedList<AnnotatedElement> elements = new LinkedList<>();
        elements.add(field.getAnnotatedType());

        int count = 0;
        while (!elements.isEmpty()) try {
            @NotNull AnnotatedElement element = elements.poll();

            if (element instanceof AnnotatedType) {
                @NotNull AnnotatedType annotated = (AnnotatedType) element;
                @NotNull Type type = annotated.getType();

                if (element instanceof AnnotatedParameterizedType) {
                    @NotNull AnnotatedParameterizedType parameterized = (AnnotatedParameterizedType) element;
                    elements.addAll(Arrays.asList(parameterized.getAnnotatedActualTypeArguments()));
                }

                // Skip the first annotated element values to not catch field concretes
                if (count == 0) {
                    continue;
                }

                genericConcretes.putIfAbsent(type, new LinkedHashSet<>());
                if (type instanceof Class && isConcrete((Class<?>) type)) {
                    genericConcretes.get(type).add((Class<?>) type);
                } if (annotated.isAnnotationPresent(Concrete.class)) {
                    genericConcretes.get(type).add(annotated.getAnnotation(Concrete.class).type());
                } if (annotated.isAnnotationPresent(Concretes.class)) {
                    genericConcretes.get(type).addAll(Arrays.stream(annotated.getAnnotationsByType(Concretes.class)).flatMap(concretes -> Arrays.stream(concretes.value())).map(Concrete::type).collect(Collectors.toList()));
                }
            }
        } finally {
            count++;
        }

        // Context factory
        @NotNull ContextFactory contextFactory;

        if (field.isAnnotationPresent(UsingSerializers.class)) {
            contextFactory = ContextFactory.methods(field.getDeclaringClass(), field.getAnnotation(UsingSerializers.class));
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
        @Nullable Adapter adapter;

        if (!typeConcretes.isEmpty()) {
            adapter = serializer.getAdapter(typeConcretes.stream().findFirst().orElseThrow(NullPointerException::new)).orElse(null);
        } else {
            adapter = serializer.getAdapter(field.getType()).orElse(null);
        }

        // Fields
        @NotNull Set<Field> includedFields = new LinkedHashSet<>(getFields(father, reference).values());

        // @ExcludeFields and @OnlyFields annotation
        if (father.getField().isAnnotationPresent(OnlyFields.class)) {
            @NotNull Set<String> only = father.getField().isAnnotationPresent(OnlyFields.class) ? new HashSet<>(Arrays.asList(father.getField().getAnnotation(OnlyFields.class).fields())) : new HashSet<>();
            includedFields.removeIf(f -> !only.contains(f.getName()));
        } else if (father.getField().isAnnotationPresent(ExcludeFields.class)) {
            @NotNull Set<String> excluded = father.getField().isAnnotationPresent(ExcludeFields.class) ? new HashSet<>(Arrays.asList(father.getField().getAnnotation(ExcludeFields.class).fields())) : new HashSet<>();
            includedFields.removeIf(f -> excluded.contains(f.getName()));
        }

        // Finish
        this.father = father;
        this.outerInstance = null;
        this.typeConcretes.addAll(typeConcretes);
        this.genericConcretes.putAll(genericConcretes);
        this.bypassTransients = bypassTransients;
        this.includedFields.addAll(includedFields);
        this.contextFactory = contextFactory;
        this.instanceFactory = instanceFactory;
        this.adapter = adapter;
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
    public @NotNull Builder outerInstance(@Nullable Object outerInstance) {
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

    @Contract(value = "_->this")
    public @NotNull Builder ignoreCasting(boolean ignoreCasting) {
        this.ignoreCasting = ignoreCasting;
        return this;
    }

    // Builder

    public @NotNull Config build() {
        return new ConfigImpl(father, outerInstance, typeConcretes, genericConcretes, bypassTransients, includedFields, contextFactory, instanceFactory, adapter, ignoreCasting);
    }

}
