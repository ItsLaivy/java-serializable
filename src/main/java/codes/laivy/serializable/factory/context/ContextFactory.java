package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;

// todo: IdentifierSerialization
//       It will allow to serialize/deserialize using id fields
public interface ContextFactory {

    // Static initializers

    static @NotNull ContextFactory field() {
        return new NativeContextFactory();
    }
    static @NotNull ContextFactory methods(@NotNull Method serializer, @NotNull Method deserializer) {
        return new MethodsContextFactory(serializer, deserializer, true);
    }
    static @NotNull ContextFactory methods(@NotNull Class<?> declaringClass, @NotNull MethodSerialization annotation) {
        return new MethodsContextFactory(MethodsContextFactory.getSerializerMethod(declaringClass, annotation), MethodsContextFactory.getDeserializerMethod(declaringClass, annotation), false);
    }

    // Object

    @Nullable Object write(@NotNull Class<?> reference, @Nullable Object object, @NotNull Serializer serializer, @NotNull Config config);
    @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws IOException, InstantiationException;

}
