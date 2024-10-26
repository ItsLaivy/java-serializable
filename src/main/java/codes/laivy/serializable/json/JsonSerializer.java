package codes.laivy.serializable.json;

import codes.laivy.serializable.AbstractTypeSerializer;
import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.utilities.Classes;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
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
        @NotNull ContextFactory contextFactory = config.getContextFactory();

        // Check nullability
        if (object == null) {
            return JsonNull.INSTANCE;
        } else if (object instanceof Context) {
            throw new IllegalArgumentException("you should use #serialize(Class, Context) to serialize contexts!");
        }

        // Adapters
        @NotNull Class<?> reference = object.getClass();
        @Nullable Adapter adapter = config.getAdapter();

        if (adapter != null) {
            return serialize(adapter.write(object, this, config));
        }

        // Serialize
        @NotNull Context context = contextFactory.write(object, this, config);
        return serialize(context);
    }

    // Deserialization

    @SuppressWarnings("unchecked")
    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context, @NotNull Config config) {
        // Start deserialization with compatible reference
        if (!Classes.isConcrete(reference)) {
            throw new IllegalArgumentException("the references should be all concretes: '" + reference.getName() + "'");
        }

        // Adapters
        @Nullable Adapter adapter = config.getAdapter();

        if (adapter != null) try {
            return (E) adapter.read(reference, this, context, config);
        } catch (@NotNull EOFException e) {
            // todo: exception message
            throw new RuntimeException(e);
        }

        if (context.isNullContext()) {
            return null;
        }

        // Factory
        @NotNull ContextFactory factory = config.getContextFactory();

        // Deserialize with factory
        try {
            @Nullable Object deserialized = factory.read(reference, this, context, config);

            // todo: check type
            return (E) deserialized;
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

    // Context

    @Override
    public @NotNull Context toContext(@Nullable Object object, @NotNull Config config) {
        if (object == null) {
            return NullContext.create();
        } else {
            @NotNull Class<?> reference = object.getClass();

            if (adapters.map.containsKey(reference)) {
                return adapters.map.get(reference).write(object, this, config);
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
                @Nullable ContextFactory factory = config.getContextFactory();
                return factory.write(object, this, config);
            }
        }
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull Context context) {
        if (context.isNullContext()) {
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
