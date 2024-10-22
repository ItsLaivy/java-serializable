package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.UsingSerializers;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedSerializerException;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

final class MethodsContextFactory implements ContextFactory {

    // Static initializers

    private static boolean checkSerializerMethod(@NotNull Method method, @NotNull String name) {
        return method.getName().equals(name) &&
                Modifier.isStatic(method.getModifiers()) &&
                (method.getParameterCount() == 1 || (method.getParameterCount() == 2 && method.getParameters()[1].getType().isAssignableFrom(SerializationProperties.class))) &&
                Context.class.isAssignableFrom(method.getReturnType());
    }
    private static boolean checkDeserializerMethod(@NotNull Method method, @NotNull String name) {
        return method.getName().equals(name) &&
                Modifier.isStatic(method.getModifiers()) &&
                method.getReturnType() != void.class &&
                (method.getParameterCount() == 1 && method.getParameters()[0].getType().isAssignableFrom(Context.class)) &&
                Context.class.isAssignableFrom(method.getParameters()[0].getType());
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
            if (checkSerializerMethod(method, name)) {
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
            if (checkDeserializerMethod(method, name)) {
                return method;
            }
        }

        throw new MalformedSerializerException("there's no valid deserialize method named '" + name + "' at class '" + declaringClass + "'");
    }

    // Object

    private final @NotNull Method serializer;
    private final @NotNull Method deserializer;

    MethodsContextFactory(@NotNull Method serializer, @NotNull Method deserializer, boolean verify) {
        this.serializer = serializer;
        this.deserializer = deserializer;

        if (verify) {
            // Checks
            if (!checkSerializerMethod(serializer, serializer.getName())) {
                throw new IllegalArgumentException("this serializer method '" + serializer + "' is invalid!");
            } else if (!checkSerializerMethod(deserializer, serializer.getName())) {
                throw new IllegalArgumentException("this deserializer method '" + deserializer + "' is invalid!");
            }
        }
    }
    public MethodsContextFactory(@NotNull Method serializer, @NotNull Method deserializer) {
        this(serializer, deserializer, true);
    }

    // Modules

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        try {
            if (!this.serializer.getParameters()[0].getType().isAssignableFrom(object.getClass())) {
                throw new UnsupportedOperationException("the serializer cannot be used by reference '" + object.getClass() + "' because it's not a subclass/implementation from '" + this.serializer.getParameters()[0].getType() + "' parameter class");
            }

            // Start serialize
            this.serializer.setAccessible(true);

            if (this.serializer.getParameterCount() == 1) {
                return (Context) this.serializer.invoke(null, object);
            } else {
                return (Context) this.serializer.invoke(null, object, properties);
            }
        } catch (@NotNull IllegalAccessException | @NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot execute serialize method from @UsingSerializers annotation", e);
        }
    }
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException, InstantiationException {
        try {
            if (!deserializer.getReturnType().isAssignableFrom(reference)) {
                throw new UnsupportedOperationException("the deserializer cannot be used by reference '" + reference + "' because it's not a subclass/implementation from '" + deserializer.getReturnType() + "' return class");
            }

            // Start deserialize
            deserializer.setAccessible(true);
            return deserializer.invoke(null, context);
        } catch (@NotNull IllegalAccessException | @NotNull InvocationTargetException e) {
            throw new RuntimeException("cannot execute deserialize method from @UsingSerializers annotation", e);
        }
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MethodsContextFactory)) return false;
        @NotNull MethodsContextFactory that = (MethodsContextFactory) object;
        return Objects.equals(serializer, that.serializer) && Objects.equals(deserializer, that.deserializer);
    }
    @Override
    public int hashCode() {
        return Objects.hash(serializer, deserializer);
    }

}
