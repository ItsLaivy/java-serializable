package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.annotations.Concretes;
import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.exception.IllegalConcreteTypeException;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.exception.MalformedSerializerException;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static codes.laivy.serializable.utilities.Classes.isConcrete;

// todo: The #serialize or #deserialize methods are optional, but if try to serialize/deserialize without the respectively methods it will thrown an exception
public final class MethodsContextFactory implements ContextFactory {

    // Static initializers

    private static @NotNull Class<?> @NotNull [] getType(@NotNull Parameter parameter) throws IllegalConcreteTypeException {
        @NotNull Class<?> type = parameter.getType();
        @NotNull Set<Class<?>> set = new LinkedHashSet<>();

        if (type == Context.class) {
            return new Class[] { type };
        } else if (isConcrete(type)) {
            set.add(type);
        }

        if (parameter.isAnnotationPresent(Concrete.class)) {
            set.addAll(Arrays.stream(parameter.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toList()));
        } if (parameter.isAnnotationPresent(Concretes.class)) {
            set.addAll(Arrays.stream(parameter.getAnnotationsByType(Concretes.class)).flatMap(array -> Arrays.stream(array.value())).map(Concrete::type).collect(Collectors.toList()));
        }

        for (@NotNull Class<?> reference : set) {
            if (!type.isAssignableFrom(reference)) {
                throw new IllegalConcreteTypeException("the concrete annotation value '" + reference.getName() + "' isn't assignable from '" + type.getName() + "'");
            }
        }

        return set.toArray(new Class[0]);
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean checkSerializerMethod(@NotNull Serializer serializer, @NotNull Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            return false; // Must be static
        } else if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
            return false; // Cannot return void
        } else if (method.getParameterCount() == 2) {
            // The first and second parameter must be (Class and Object (any type)) OR (Object (any type) and Config)
            if (method.getParameters()[0].getType() == Class.class) {
                return true;
            } else if (method.getParameters()[1].getType() == Config.class) {
                return true;
            } else {
                return false;
            }
        } else if (method.getParameterCount() == 3) {
            // The parameters should be: Class, Object (any type) and Config
            if (method.getParameters()[0].getType() != Class.class) {
                return false;
            } else if (method.getParameters()[2].getType() != Config.class) {
                return false;
            }
        } else if (method.getParameterCount() != 1) {
            return false;
        }

        return true;
    }
    @SuppressWarnings("RedundantIfStatement")
    private static boolean checkDeserializerMethod(@NotNull Serializer serializer, @NotNull Method method) {
        // Check object parameter's concrete reference
        @NotNull Predicate<Parameter> predicate = parameter -> {
            @NotNull Class<?> reference = parameter.getType();

            if (reference == Context.class) {
                return true;
            } else if (ArrayContext.class.isAssignableFrom(reference)) {
                return true;
            } else if (MapContext.class.isAssignableFrom(reference)) {
                return true;
            } else if (PrimitiveContext.class.isAssignableFrom(reference)) {
                return true;
            } else if (NullContext.class.isAssignableFrom(reference)) {
                return true;
            } else if (Context.class.isAssignableFrom(reference)) {
                throw new UnsupportedOperationException("illegal context type '" + reference + "'. You should only use Context, ArrayContext, MapContext, PrimitiveContext or NullContext");
            } else if (Arrays.stream(getType(parameter)).anyMatch(Classes::isConcrete)) {
                return true;
            } else if (serializer.getAdapter(parameter.getType()).isPresent()) {
                return true;
            }

            return false;
        };

        // Validation
        if (!Modifier.isStatic(method.getModifiers())) {
            return false; // Must be static
        } else if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
            return false; // Cannot return void
        } else if (method.getParameterCount() == 1) {
            // The parameter must be a context here
            if (!predicate.test(method.getParameters()[0])) {
                return false;
            }
        } else if (method.getParameterCount() == 2) {
            // The first and second parameter must be (Class and Context) OR (Context and Config)
            if (method.getParameters()[0].getType() == Class.class && predicate.test(method.getParameters()[1])) {
                return true;
            } else if (predicate.test(method.getParameters()[0]) && method.getParameters()[1].getType() == Config.class) {
                return true;
            } else {
                return false;
            }
        } else if (method.getParameterCount() == 3) {
            // The parameters should be: Class, Context and Config
            if (method.getParameters()[0].getType() != Class.class) {
                return false;
            } else if (!predicate.test(method.getParameters()[1])) {
                return false;
            } else if (method.getParameters()[2].getType() != Config.class) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public static @NotNull Method getSerializerMethod(@NotNull Serializer serializer, @NotNull Class<?> declaringClass, @NotNull MethodSerialization annotation) {
        @NotNull String string = annotation.serialization();
        @NotNull String[] parts = string.split("#");
        @NotNull String name;

        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("illegal serializer method reference '" + string + "'");
        } else if (parts.length == 2) try {
            if (!parts[0].isEmpty()) {
                declaringClass = Class.forName(parts[0]);
            }

            name = parts[1];
        } catch (@NotNull ClassNotFoundException e) {
            throw new RuntimeException("cannot find class '" + parts[0] + "' from serialize method at @MethodSerialization annotation", e);
        } else {
            name = parts[0];
        }

        // Get methods
        for (@NotNull Method method : declaringClass.getDeclaredMethods()) {
            if (method.getName().equals(name) && checkSerializerMethod(serializer, method)) {
                return method;
            }
        }

        throw new MalformedSerializerException("there's no valid serialize method named '" + name + "' at class '" + declaringClass + "'");
    }
    public static @NotNull Method getDeserializerMethod(@NotNull Serializer serializer, @NotNull Class<?> declaringClass, @NotNull MethodSerialization annotation) {
        @NotNull String string = annotation.deserialization();
        @NotNull String[] parts = string.split("#");
        @NotNull String name;

        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("illegal serializer method reference '" + string + "'");
        } else if (parts.length == 2) try {
            if (!parts[0].isEmpty()) {
                declaringClass = Class.forName(parts[0]);
            }

            name = parts[1];
        } catch (@NotNull ClassNotFoundException e) {
            throw new RuntimeException("cannot find class '" + parts[0] + "' from deserialize method at @MethodSerialization annotation", e);
        } else {
            name = parts[0];
        }

        // Get methods
        for (@NotNull Method method : declaringClass.getDeclaredMethods()) {
            if (method.getName().equals(name) && checkDeserializerMethod(serializer, method)) {
                return method;
            }
        }

        throw new MalformedSerializerException("there's no valid deserialize method named '" + name + "' at class '" + declaringClass + "'");
    }

    // Object

    private final @NotNull Serialization serialization;
    private final @NotNull Deserialization deserialization;

    MethodsContextFactory(@NotNull Serializer serializer, @NotNull Method serializeMethod, @NotNull Method deserializeMethod, boolean verify) {
        if (verify) {
            // Checks
            if (!checkSerializerMethod(serializer, serializeMethod)) {
                throw new IllegalArgumentException("this serializer method '" + serializeMethod + "' is invalid!");
            } else if (!checkDeserializerMethod(serializer, deserializeMethod)) {
                throw new IllegalArgumentException("this deserializer method '" + deserializeMethod + "' is invalid!");
            }
        }

        this.serialization = new Serialization(serializeMethod);
        this.deserialization = new Deserialization(deserializeMethod);
    }
    public MethodsContextFactory(@NotNull Serializer serializer, @NotNull Method serializeMethod, @NotNull Method deserializeMethod) {
        this(serializer, serializeMethod, deserializeMethod, true);
    }

    // Getters

    public @NotNull Method getSerialization() {
        return serialization.getMethod();
    }
    public @NotNull Method getDeserialization() {
        return deserialization.getMethod();
    }

    // Modules

    @Override
    public @Nullable Object write(@NotNull Class<?> reference, @Nullable Object object, @NotNull Serializer serializer, @NotNull Config config) {
        try {
            @NotNull Method method = serialization.getMethod();
            @Nullable Object serialized = serialization.call(reference, object, config);

            if (serialized != null) reference = serialized.getClass();
            else reference = method.getReturnType();

            // todo: Improve this recurring serialization system
            if (serialized instanceof Context) {
                return serialized;
            } else {
                @NotNull Config temp = Config.builder(serializer, reference).addGenericConcrete(Classes.getGenericTypes(method.getAnnotatedReturnType())).build();
                return serializer.serialize(serialized, temp);
            }
        } catch (@NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot execute serialize method from @MethodSerialization annotation", e);
        }
    }
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws EOFException, InstantiationException {
        try {
            return deserialization.call(reference, serializer, context, config);
        } catch (@NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot execute deserialize method from @MethodSerialization annotation", e);
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MethodsContextFactory)) return false;
        @NotNull MethodsContextFactory that = (MethodsContextFactory) object;
        return Objects.equals(serialization, that.serialization) && Objects.equals(deserialization, that.deserialization);
    }
    @Override
    public int hashCode() {
        return Objects.hash(serialization, deserialization);
    }

    @Override
    public @NotNull String toString() {
        return "MethodsContextFactory{" +
                "serialization=" + serialization.getMethod() +
                ", deserialization=" + deserialization.getMethod() +
                '}';
    }

    // Classes

    private static final class Serialization {

        private final @NotNull Method method;

        public Serialization(@NotNull Method method) {
            this.method = method;
        }

        // Getters

        public @NotNull Method getMethod() {
            return method;
        }

        // Modules

        private void verify(@NotNull Class<?> reference) {
            int index;
            if (method.getParameterCount() == 1) {
                index = 0;
            } else if (method.getParameterCount() == 2) {
                if (method.getParameters()[0].getType() == Class.class) {
                    index = 1;
                } else {
                    index = 0;
                }
            } else {
                index = 1;
            }

            if (!method.getParameters()[index].getType().isAssignableFrom(reference)) {
                throw new UnsupportedOperationException("the serializer method '" + method + "' cannot be used by reference '" + reference + "' because it's not a subclass/implementation from '" + method.getParameters()[index].getType() + "' parameter class");
            }
        }
        public @Nullable Object call(@NotNull Class<?> reference, @Nullable Object object, @NotNull Config config) throws InvocationTargetException {
            // Verifications
            verify(reference);

            // Perform serialization
            try {
                boolean accessible = method.isAccessible();
                method.setAccessible(true);

                try {
                    if (method.getParameterCount() == 1) {
                        return method.invoke(null, object);
                    } else if (method.getParameterCount() == 2) {
                        if (method.getParameters()[0].getType() == Class.class) {
                            return method.invoke(null, reference, object);
                        } else {
                            return method.invoke(null, object, config);
                        }
                    } else if (method.getParameterCount() == 3) {
                        return method.invoke(null, reference, object, config);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } finally {
                    method.setAccessible(accessible);
                }
            } catch (@NotNull IllegalAccessException e) {
                throw new RuntimeException("cannot access serialization method '" + method + "'", e);
            }
        }

    }
    private static final class Deserialization {

        private final @NotNull Method method;

        public Deserialization(@NotNull Method method) {
            this.method = method;
        }

        // Getters

        public @NotNull Method getMethod() {
            return method;
        }

        // Modules

        public @Nullable Object call(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws InvocationTargetException {
            // Check if the parameter is compatible with the object
            @NotNull Function<@NotNull Parameter, @Nullable Object> parameter = new Function<Parameter, Object>() {
                @Override
                public @Nullable Object apply(@NotNull Parameter parameter) {
                    if (parameter.getType() == Context.class) {
                        return context;
                    } else if (ArrayContext.class.isAssignableFrom(parameter.getType())) {
                        if (!context.isArray()) {
                            throw new IncompatibleReferenceException("to deserialize an array context the context must be an array: " + context);
                        }

                        return context.getAsArray();
                    } else if (MapContext.class.isAssignableFrom(parameter.getType())) {
                        if (!context.isMap()) {
                            throw new IncompatibleReferenceException("to deserialize a map context the context must be a map: " + context);
                        }

                        return context.getAsMap();
                    } else if (NullContext.class.isAssignableFrom(parameter.getType())) {
                        if (!context.isNull()) {
                            throw new IncompatibleReferenceException("to deserialize a null context the context must be a null: " + context);
                        }

                        return context.getAsNull();
                    } else if (PrimitiveContext.class.isAssignableFrom(parameter.getType())) {
                        if (!context.isPrimitive()) {
                            throw new IncompatibleReferenceException("to deserialize a primitive context the context must be a primitive: " + context);
                        }

                        return context.getAsPrimitive();
                    } else if (Context.class.isAssignableFrom(parameter.getType())) {
                        throw new UnsupportedOperationException("illegal context type '" + parameter.getType() + "'. You should only use Context, ArrayContext, MapContext, PrimitiveContext or NullContext");
                    } else {
                        @NotNull Class<?>[] classes = getType(parameter);

                        if (classes.length == 0) {
                            throw new IllegalConcreteTypeException("there's no available concrete reference available at #" + method.getName() + " parameter: " + parameter.getName());
                        }

                        for (@NotNull Class<?> reference : classes) try {
                            @Nullable Object object = serializer.deserialize(reference, context);
                            return object;
                        } catch (@NotNull IncompatibleReferenceException ignore) {
                        }

                        throw new IncompatibleReferenceException("there's no available concrete reference at #" + method.getName() + " method parameter '" + parameter.getName() + "', tried with classes: " + Arrays.toString(classes) + ", to deserialize context: " + context);
                    }
                }
            };

            // Check if return type is assignable
            if (!method.getReturnType().isAssignableFrom(reference)) {
                throw new UnsupportedOperationException("the deserializer method '" + method + "' cannot be used to deserialize reference '" + reference.getName() + "' because it's not a subclass/implementation from '" + method.getReturnType() + "' return class");
            }

            // Try deserialize
            try {
                boolean accessible = method.isAccessible();
                method.setAccessible(true);

                try {
                    if (method.getParameterCount() == 1) {
                        return method.invoke(null, parameter.apply(method.getParameters()[0]));
                    } else if (method.getParameterCount() == 2) {
                        if (method.getParameters()[0].getType() == Class.class) {
                            return method.invoke(null, reference, parameter.apply(method.getParameters()[1]));
                        } else {
                            return method.invoke(null, parameter.apply(method.getParameters()[0]), config);
                        }
                    } else if (method.getParameterCount() == 3) {
                        return method.invoke(null, reference, parameter.apply(method.getParameters()[1]), config);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } finally {
                    method.setAccessible(accessible);
                }
            } catch (@NotNull IllegalAccessException e) {
                throw new RuntimeException("cannot access serialization method '" + method + "'", e);
            }
        }

    }

}
