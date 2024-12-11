package codes.laivy.serializable.config;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.BypassTransient;
import codes.laivy.serializable.annotations.ExcludeFields;
import codes.laivy.serializable.annotations.OnlyFields;
import codes.laivy.serializable.annotations.UseEmptyConstructor;
import codes.laivy.serializable.annotations.serializers.EnheritSerialization;
import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import codes.laivy.serializable.config.Config.Father;
import codes.laivy.serializable.exception.IllegalConcreteTypeException;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

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
        @NotNull Class<?> enclosing = reference.isAnonymousClass() ? reference.getEnclosingClass() : reference;
        @NotNull Set<Class<?>> references = Classes.getReferences(enclosing);

        // Adapter
        @Nullable Adapter adapter = serializer.getAdapter(enclosing).orElse(null);

        // Check @EnheritSerializers annotations
        @Nullable MethodSerializer serializers = getSerializers(enclosing);

        // Factories
        @NotNull InstanceFactory instanceFactory;
        @NotNull ContextFactory contextFactory;

        if (serializers != null) {
            contextFactory = ContextFactory.methods(serializer, serializers.reference, serializers.methodSerialization);
        } else {
            if (adapter == null && references.size() == 1 && !isConcrete(enclosing)) {
                throw new IllegalConcreteTypeException("this reference '" + enclosing.getName() + "' isn't concrete. Try to include @Concrete annotations");
            }

            contextFactory = ContextFactory.field();
        }

        if (enclosing.isAnnotationPresent(UseEmptyConstructor.class)) {
            instanceFactory = InstanceFactory.constructor();
        } else {
            instanceFactory = InstanceFactory.allocator();
        }

        // Finish
        this.father = null;
        this.outerInstance = null;
        this.typeConcretes.addAll(references);
        this.bypassTransients = false;
        this.includedFields.addAll(getFields(null, enclosing).values());
        this.contextFactory = contextFactory;
        this.instanceFactory = instanceFactory;
        this.adapter = adapter;
        this.ignoreCasting = true;
    }
    Builder(@NotNull Serializer serializer, @NotNull Class<?> reference, @NotNull Father father) {
        @NotNull Field field = father.getField();
        @NotNull Class<?> enclosing = reference.isAnonymousClass() ? reference.getEnclosingClass() : reference;

        boolean bypassTransients = field.isAnnotationPresent(BypassTransient.class);

        // Concretes
        @NotNull Set<Class<?>> typeConcretes = new LinkedHashSet<>();
        typeConcretes.add(enclosing);
        typeConcretes.addAll(Classes.getReferences(father.getField()));

        // Generics
        @NotNull Map<Type, Collection<Class<?>>> genericConcretes = Classes.getGenericTypes(field.getAnnotatedType());

        // Context factory
        @Nullable MethodSerializer serializers = field.isAnnotationPresent(MethodSerialization.class) ? new MethodSerializer(field.getDeclaringClass(), field.getAnnotation(MethodSerialization.class)) : getSerializers(enclosing);
        @NotNull ContextFactory contextFactory;

        if (serializers != null) {
            contextFactory = ContextFactory.methods(serializer, serializers.reference, serializers.methodSerialization);
        } else {
            contextFactory = ContextFactory.field();
        }

        // Instance factory
        @NotNull InstanceFactory instanceFactory;

        if (field.isAnnotationPresent(UseEmptyConstructor.class) || enclosing.isAnnotationPresent(UseEmptyConstructor.class)) {
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
        @NotNull Set<Field> includedFields = new LinkedHashSet<>(getFields(father, enclosing).values());

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
    public @NotNull Builder addTypeConcrete(@NotNull Class<?> reference) {
        typeConcretes.add(reference);
        return this;
    }
    @Contract(value = "_,_->this")
    public @NotNull Builder addGenericConcrete(@NotNull Type type, @NotNull Class<?> reference) {
        genericConcretes.putIfAbsent(type, new LinkedList<>());
        genericConcretes.get(type).add(reference);

        return this;
    }
    @Contract(value = "_->this")
    public @NotNull Builder addGenericConcrete(@NotNull Map<Type, Collection<Class<?>>> map) {
        genericConcretes.putAll(map);
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
        return new ConfigImpl(father, outerInstance, typeConcretes, genericConcretes, bypassTransients, includedFields, contextFactory, instanceFactory, adapter, ignoreCasting, true);
    }

    // Utilities

    private static @Nullable MethodSerializer getSerializers(@NotNull Class<?> reference) {
        // Functions
        @NotNull Function<Class<?>, Void> verification = ref -> {
            if (reference.isAnnotationPresent(MethodSerialization.class) && reference.isAnnotationPresent(EnheritSerialization.class)) {
                throw new IllegalStateException("the reference '" + reference.getName() + "' includes both @EnheritSerializers and @MethodSerialization annotation!");
            }
            return null;
        };

        // Retrieve method serializer instance
        @NotNull Class<?> owner = reference;
        @Nullable MethodSerialization methodSerialization = null;
        verification.apply(reference);

        if (reference.isAnnotationPresent(EnheritSerialization.class)) {
            @NotNull Class<?> sub = reference.getSuperclass();

            while (sub != Object.class) {
                verification.apply(sub);

                if (sub.isAnnotationPresent(EnheritSerialization.class)) {
                    sub = sub.getSuperclass();
                } else if (sub.isAnnotationPresent(MethodSerialization.class)) {
                    methodSerialization = sub.getAnnotation(MethodSerialization.class);
                    owner = sub;
                    break;
                } else {
                    throw new IllegalStateException("there's no valid @MethodSerialization found at reference's super classes '" + reference.getName() + "'");
                }
            }
        } else if (reference.isAnnotationPresent(MethodSerialization.class)) {
            methodSerialization = reference.getAnnotation(MethodSerialization.class);
        }

        // Finish
        return methodSerialization != null ? new MethodSerializer(owner, methodSerialization) : null;
    }
    private static final class MethodSerializer {
        private final @NotNull Class<?> reference;
        private final @NotNull MethodSerialization methodSerialization;

        public MethodSerializer(@NotNull Class<?> reference, @NotNull MethodSerialization methodSerialization) {
            this.reference = reference;
            this.methodSerialization = methodSerialization;
        }
    }

}
