package codes.laivy.serializable.json;

import codes.laivy.serializable.AbstractTypeSerializer;
import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.utilities.Classes;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;

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

        // Adapters
        @NotNull Class<?> reference = object.getClass();
        @Nullable Adapter adapter = config.getAdapter();
        @NotNull ContextFactory contextFactory = config.getContextFactory();

        if (adapter != null) {
            contextFactory = adapter;
        }

        // Serialize
        @Nullable Object response = contextFactory.write(reference, object, this, config);

        if (response instanceof Context) {
            return serialize((Context) response);
        } else {
            // Serialize again
            return serialize(response);
        }
    }

    // Deserialization

    @Override
    public @Nullable Object deserializeUnsafe(@NotNull Class<?> reference, @NotNull Context context, @NotNull Config config) throws IncompatibleReferenceException {
        // Start deserialization with compatible reference
        if (!Classes.isConcrete(reference)) {
            throw new IllegalArgumentException("the references should be all concretes: '" + reference.getName() + "'");
        }

        // Adapters and factory
        @NotNull ContextFactory factory;

        if (config.getAdapter() != null) factory = config.getAdapter();
        else factory = config.getContextFactory();

        // Deserialize with factory
        try {
            return factory.read(reference, this, context, config);
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        } catch (@NotNull InstantiationException e) {
            throw new RuntimeException("cannot instantiate '" + reference.getName() + "'", e);
        }
    }

    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable JsonElement element, @NotNull Config config) {
        return deserialize(reference, toContext(element), config);
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
            @NotNull Class<?> reference = object.getClass();
            @NotNull ContextFactory contextFactory;

            if (adapters.map.containsKey(reference)) {
                contextFactory = adapters.map.get(reference);
            } else if (object instanceof Context) {
                throw new IllegalArgumentException("you cannot convert a context into a context");
            } else if (object instanceof Enum<?>) {
                return PrimitiveContext.create(((Enum<?>) object).name());
            } else if (object instanceof Boolean) {
                return PrimitiveContext.create((Boolean) object);
            } else if (object instanceof Short) {
                return PrimitiveContext.create((Short) object);
            } else if (object instanceof Integer) {
                return PrimitiveContext.create((Integer) object);
            } else if (object instanceof Long) {
                return PrimitiveContext.create((Long) object);
            } else if (object instanceof Float) {
                return PrimitiveContext.create((Float) object);
            } else if (object instanceof Double) {
                return PrimitiveContext.create((Double) object);
            } else if (object instanceof Character) {
                return PrimitiveContext.create((Character) object);
            } else if (object instanceof Byte) {
                return PrimitiveContext.create((Byte) object);
            } else if (object instanceof String) {
                return PrimitiveContext.create((String) object);
            } else if (reference.isArray()) {
                @NotNull ArrayContext context = ArrayContext.create(this);
                final int length = Array.getLength(object);

                for (int index = 0; index < length; index++) {
                    @Nullable Object element = Array.get(object, index);
                    context.write(toContext(element));
                }

                return context;
            } else {
                contextFactory = config.getContextFactory();
            }

            // Generate using context factory
            @Nullable Object instance = contextFactory.write(reference, object, this, config);

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

}
