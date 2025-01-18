package codes.laivy.serializable.json;

import codes.laivy.serializable.AbstractTypeSerializer;
import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.factory.context.ContextFactory;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class JsonSerializer extends AbstractTypeSerializer<JsonElement> {

    // Static initializers

    @SuppressWarnings("FieldMayBeFinal")
    private static @NotNull JsonSerializer instance = new JsonSerializer();

    public static @NotNull JsonSerializer getInstance() {
        return instance;
    }

    // Serialization

    @Override
    public @NotNull JsonElement serialize(@Nullable Object object, @NotNull Config config) {
        // Check nullability
        if (object == null) {
            return JsonNull.INSTANCE;
        } else if (object instanceof Context) {
            throw new IllegalArgumentException("you should use #serialize(Class, Context) to serialize contexts!");
        }

        // Serialize
        @Nullable Object response = config.getContextFactory().write(object.getClass(), object, this, config);

        if (response instanceof Context) {
            return serialize((Context) response);
        } else {
            @Nullable JsonElement element;

            if (object instanceof Enum) {
                element = serialize((Enum<?>) object);
            } else if (object instanceof Enum[]) {
                element = (JsonArray) serialize((Enum<?>[]) object);
            } else if (object instanceof Boolean) {
                element = serialize((Boolean) object);
            } else if (object instanceof Boolean[]) {
                element = (JsonArray) serialize((Boolean[]) object);
            } else if (object instanceof boolean[]) {
                element = (JsonArray) serialize((boolean[]) object);
            } else if (object instanceof Short) {
                element = serialize((Short) object);
            } else if (object instanceof Short[]) {
                element = (JsonArray) serialize((Short[]) object);
            } else if (object instanceof short[]) {
                element = (JsonArray) serialize((short[]) object);
            } else if (object instanceof Byte) {
                element = serialize((Byte) object);
            } else if (object instanceof Byte[]) {
                element = (JsonArray) serialize((Byte[]) object);
            } else if (object instanceof byte[]) {
                element = (JsonArray) serialize((byte[]) object);
            } else if (object instanceof Integer) {
                element = serialize((Integer) object);
            } else if (object instanceof Integer[]) {
                element = (JsonArray) serialize((Integer[]) object);
            } else if (object instanceof int[]) {
                element = (JsonArray) serialize((int[]) object);
            } else if (object instanceof Long) {
                element = serialize((Long) object);
            } else if (object instanceof Long[]) {
                element = (JsonArray) serialize((Long[]) object);
            } else if (object instanceof long[]) {
                element = (JsonArray) serialize((long[]) object);
            } else if (object instanceof Float) {
                element = serialize((Float) object);
            } else if (object instanceof Float[]) {
                element = (JsonArray) serialize((Float[]) object);
            } else if (object instanceof float[]) {
                element = (JsonArray) serialize((float[]) object);
            } else if (object instanceof Double) {
                element = serialize((Double) object);
            } else if (object instanceof Double[]) {
                element = (JsonArray) serialize((Double[]) object);
            } else if (object instanceof double[]) {
                element = (JsonArray) serialize((double[]) object);
            } else if (object instanceof Character) {
                element = serialize((Character) object);
            } else if (object instanceof Character[]) {
                element = (JsonArray) serialize((Character[]) object);
            } else if (object instanceof char[]) {
                element = (JsonArray) serialize((char[]) object);
            } else if (object instanceof String) {
                element = serialize((String) object);
            } else if (object instanceof String[]) {
                element = (JsonArray) serialize((String[]) object);
            } else {
                element = serialize(response);
            }

            return element != null ? element : JsonNull.INSTANCE;
        }
    }

    // Deserialization

    @Override
    public @Nullable Object deserializeUnsafe(@NotNull Class<?> reference, @NotNull Context context, @NotNull Config config) throws IncompatibleReferenceException {
        if (reference == Context.class) {
            return context;
        } else if (MapContext.class.isAssignableFrom(reference)) {
            if (!context.isMap()) {
                throw new IncompatibleReferenceException("to deserialize a map context the context must be a map: " + context);
            }

            return context.getAsMap();
        } else if (ArrayContext.class.isAssignableFrom(reference)) {
            if (!context.isArray()) {
                throw new IncompatibleReferenceException("to deserialize an array context the context must be an array: " + context);
            }

            return context.getAsArray();
        } else if (PrimitiveContext.class.isAssignableFrom(reference)) {
            if (!context.isPrimitive()) {
                throw new IncompatibleReferenceException("to deserialize a primitive context the context must be a primitive: " + context);
            }

            return context.isPrimitive();
        } else if (NullContext.class.isAssignableFrom(reference)) {
            if (!context.isNull()) {
                throw new IncompatibleReferenceException("to deserialize a null context the context must be a null: " + context);
            }

            return context.getAsNull();
        } else if (Context.class.isAssignableFrom(reference)) {
            throw new UnsupportedOperationException("illegal context type '" + reference + "'. You should only use Context, ArrayContext, MapContext, PrimitiveContext or NullContext");
        } else try {
            // Deserialize with factory
            @NotNull ContextFactory factory = config.getContextFactory();
            return factory.read(reference, this, context, config);
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        } catch (@NotNull InstantiationException e) {
            throw new RuntimeException("cannot instantiate '" + reference.getName() + "'", e);
        }
    }
    @Override
    public @Nullable Object deserializeUnsafe(@NotNull Class<?> reference, @Nullable JsonElement element, @NotNull Config config) throws IncompatibleReferenceException {
        return deserializeUnsafe(reference, toContext(element), config);
    }

    // Context

    @Override
    public @NotNull Context toContext(@Nullable Object object, @NotNull Config config) {
        if (object == null) {
            return NullContext.create();
        } else {
            // Generate using context factory
            @NotNull ContextFactory factory = config.getContextFactory();
            @Nullable Object instance = factory.write(object.getClass(), object, this, config);

            if (instance instanceof Context) {
                return (Context) instance;
            } else {
                // Repeat recursively the serialization
                return toContext(instance);
            }
        }
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull Context context) {
        if (context.isNull()) {
            return JsonNull.INSTANCE;
        } else if (context instanceof PrimitiveContext) {
            @NotNull PrimitiveContext primitive = (PrimitiveContext) context;
            @NotNull Object object = primitive.getObject();
            @NotNull Class<?> reference = object.getClass();

            if (Allocator.isAssignableFromIncludingPrimitive(Boolean.class, reference)) {
                return new JsonPrimitive(primitive.getAsBoolean());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Character.class, reference)) {
                return new JsonPrimitive(primitive.getAsCharacter());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Byte.class, reference)) {
                return new JsonPrimitive(primitive.getAsByte());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Short.class, reference)) {
                return new JsonPrimitive(primitive.getAsShort());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Integer.class, reference)) {
                return new JsonPrimitive(primitive.getAsInteger());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Long.class, reference)) {
                return new JsonPrimitive(primitive.getAsLong());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Float.class, reference)) {
                return new JsonPrimitive(primitive.getAsFloat());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Double.class, reference)) {
                return new JsonPrimitive(primitive.getAsDouble());
            } else if (Allocator.isAssignableFromIncludingPrimitive(String.class, reference)) {
                return new JsonPrimitive(primitive.getAsString());
            } else if (Number.class.isAssignableFrom(reference)) {
                return new JsonPrimitive((Number) primitive.getObject());
            } else {
                throw new UnsupportedOperationException("cannot deserialize reference '" + reference + "' with a primitive context, is missing any adapter");
            }
        } else if (context instanceof ArrayContext) {
            @NotNull ArrayContext array = ((ArrayContext) context);
            @NotNull JsonArray json = new JsonArray();

            for (@NotNull Context object : array) {
                json.add(serialize(object));
            }

            return json;
        } else if (context instanceof MapContext) {
            @NotNull MapContext map = ((MapContext) context);
            @NotNull JsonObject json = new JsonObject();

            for (@NotNull String name : map.keySet()) {
                json.add(name, serialize(map.getContext(name)));
            }

            return json;
        } else {
            throw new UnsupportedOperationException("this context isn't supported by json serializer '" + context + "'");
        }
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return "JsonSerializer";
    }

}
