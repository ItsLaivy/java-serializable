package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.UsingSerializers;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;

public interface ContextFactory {

    // Static initializers

    static @NotNull ContextFactory field() {
        return new FieldContextFactory();
    }
    static @NotNull ContextFactory methods(@NotNull Method serializer, @NotNull Method deserializer) {
        return new MethodsContextFactory(serializer, deserializer, true);
    }
    static @NotNull ContextFactory methods(@NotNull Class<?> declaringClass, @NotNull UsingSerializers annotation) {
        return new MethodsContextFactory(MethodsContextFactory.getSerializerMethod(declaringClass, annotation), MethodsContextFactory.getDeserializerMethod(declaringClass, annotation), false);
    }

    // Object

    @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties);
    @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context) throws IOException, InstantiationException;

}
