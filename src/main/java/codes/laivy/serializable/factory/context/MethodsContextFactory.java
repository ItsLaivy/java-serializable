package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.UsingSerializers;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedSerializerException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class MethodsContextFactory implements ContextFactory {

    // Static initializers

    @SuppressWarnings("RedundantIfStatement")
    private static boolean checkSerializerMethod(@NotNull Method method) {
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
    private static boolean checkDeserializerMethod(@NotNull Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            return false; // Must be static
        } else if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
            return false; // Cannot return void
        } else if (method.getParameterCount() == 1) {
            // The parameter must be a context here
            if (method.getParameters()[0].getType() != Context.class) {
                return false;
            }
        } else if (method.getParameterCount() == 2) {
            // The first and second parameter must be (Class and Context) OR (Context and Config)
            if (method.getParameters()[0].getType() == Class.class && method.getParameters()[1].getType() == Context.class) {
                return true;
            } else if (method.getParameters()[0].getType() == Context.class && method.getParameters()[1].getType() == Config.class) {
                return true;
            } else {
                return false;
            }
        } else if (method.getParameterCount() == 3) {
            // The parameters should be: Class, Context and Config
            if (method.getParameters()[0].getType() != Class.class) {
                return false;
            } else if (method.getParameters()[1].getType() != Context.class) {
                return false;
            } else if (method.getParameters()[2].getType() != Config.class) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public static @NotNull Method getSerializerMethod(@NotNull Class<?> declaringClass, @NotNull UsingSerializers annotation) {
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
            throw new RuntimeException("cannot find class '" + parts[0] + "' from @UsingSerializers annotation", e);
        } else {
            name = parts[0];
        }

        // Get methods
        for (@NotNull Method method : declaringClass.getDeclaredMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }

            if (checkSerializerMethod(method)) {
                return method;
            }
        }

        throw new MalformedSerializerException("there's no valid serialize method named '" + name + "' at class '" + declaringClass + "'");
    }
    public static @NotNull Method getDeserializerMethod(@NotNull Class<?> declaringClass, @NotNull UsingSerializers annotation) {
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
            throw new RuntimeException("cannot find class '" + parts[0] + "' from @UsingSerializers annotation", e);
        } else {
            name = parts[0];
        }

        // Get methods
        for (@NotNull Method method : declaringClass.getDeclaredMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }

            if (checkDeserializerMethod(method)) {
                return method;
            }
        }

        throw new MalformedSerializerException("there's no valid deserialize method named '" + name + "' at class '" + declaringClass + "'");
    }

    // Object

    private final @NotNull Serialization serialization;
    private final @NotNull Deserialization deserialization;

    MethodsContextFactory(@NotNull Method serialization, @NotNull Method deserialization, boolean verify) {
        if (verify) {
            // Checks
            if (!checkSerializerMethod(serialization)) {
                throw new IllegalArgumentException("this serializer method '" + serialization + "' is invalid!");
            } else if (!checkSerializerMethod(deserialization)) {
                throw new IllegalArgumentException("this deserializer method '" + deserialization + "' is invalid!");
            }
        }

        this.serialization = new Serialization(serialization);
        this.deserialization = new Deserialization(deserialization);
    }
    public MethodsContextFactory(@NotNull Method serialization, @NotNull Method deserialization) {
        this(serialization, deserialization, true);
    }

    // Modules

    @Override
    public @Nullable Object write(@NotNull Class<?> reference, @Nullable Object object, @NotNull Serializer serializer, @NotNull Config config) {
        try {
            return serialization.call(reference, object, config);
        } catch (@NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot execute serialize method from @UsingSerializers annotation", e);
        }
    }
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws EOFException, InstantiationException {
        try {
            return deserialization.call(reference, context, config);
        } catch (@NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot execute deserialize method from @UsingSerializers annotation", e);
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

        public @Nullable Object call(@NotNull Class<?> reference, @Nullable Object object, @NotNull Config config) throws InvocationTargetException {
            if (!method.getReturnType().isAssignableFrom(reference)) {
                throw new UnsupportedOperationException("the deserializer method '" + method + "' cannot be used to deserialize reference '" + reference + "' because it's not a subclass/implementation from '" + method.getReturnType() + "' return class");
            }

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

}
