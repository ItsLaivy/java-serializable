package codes.laivy.serializable.json;

import codes.laivy.serializable.AbstractTypeSerializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.factory.context.FieldContextFactory;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import codes.laivy.serializable.utilities.Classes;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public final class JsonSerializer extends AbstractTypeSerializer<JsonElement> {

    // Static initializers

    @SuppressWarnings("FieldMayBeFinal")
    private static @NotNull JsonSerializer instance = new JsonSerializer();

    public static @NotNull JsonSerializer getInstance() {
        return instance;
    }

    // Serializers

    @Override
    public @NotNull JsonElement serialize(@Nullable Object object, @Nullable SerializationProperties properties) {
        // Check nullability
        if (object == null) {
            return JsonNull.INSTANCE;
        } else if (object instanceof Context) {
            throw new IllegalArgumentException("you should use #serialize(Class, Context) to serialize contexts!");
        }

        // Adapters
        @NotNull Class<?> reference = object.getClass();

        {
            @Nullable Adapter adapter = null;

            if (properties != null && properties.getAdapter() != null) {
                adapter = properties.getAdapter();
            } else if (adapters.map.containsKey(reference)) {
                adapter = adapters.map.get(reference);
            }

            if (adapter != null) {
                return serialize(adapter.write(object, this, properties));
            }
        }

        // Check native serializers
        if (reference.isEnum()) {
            return new JsonPrimitive(((Enum<?>) object).name().toUpperCase());
        } else if (reference == Boolean.class) {
            return new JsonPrimitive((Boolean) object);
        } else if (reference == Short.class) {
            return new JsonPrimitive((Short) object);
        } else if (reference == Integer.class) {
            return new JsonPrimitive((Integer) object);
        } else if (reference == Long.class) {
            return new JsonPrimitive((Long) object);
        } else if (reference == Float.class) {
            return new JsonPrimitive((Float) object);
        } else if (reference == Double.class) {
            return new JsonPrimitive((Double) object);
        } else if (reference == Character.class) {
            return new JsonPrimitive((Character) object);
        } else if (reference == Byte.class) {
            return new JsonPrimitive((Byte) object);
        } else if (reference == String.class) {
            return new JsonPrimitive((String) object);
        }

        // Context factories
        @NotNull ContextFactory factory;

        if (properties == null) {
            factory = ContextFactory.field();
        } else {
            factory = properties.getContextFactory();
        }

        // Check java serialization if context factory is fields
        if (factory instanceof FieldContextFactory && Classes.usesJavaSerialization(reference)) {
            return serialize(Classes.javaSerializeObject(this, properties, object));
        }

        // Serialize
        @NotNull Context context = factory.write(object, this, properties);
        return serialize(context);
    }
    @Override
    public @NotNull JsonElement serialize(@NotNull Context context) {
        if (context.isNullContext()) {
            return JsonNull.INSTANCE;
        } else if (context instanceof PrimitiveContext) {
            @NotNull PrimitiveContext primitive = (PrimitiveContext) context;
            @NotNull Object object = primitive.getObject();
            @NotNull Class<?> reference = object.getClass();

            if (reference == Boolean.class) {
                return new JsonPrimitive(primitive.getAsBoolean());
            } else if (reference == Character.class) {
                return new JsonPrimitive(primitive.getAsCharacter());
            } else if (reference == Byte.class) {
                return new JsonPrimitive(primitive.getAsByte());
            } else if (reference == Short.class) {
                return new JsonPrimitive(primitive.getAsShort());
            } else if (reference == Integer.class) {
                return new JsonPrimitive(primitive.getAsInteger());
            } else if (reference == Long.class) {
                return new JsonPrimitive(primitive.getAsLong());
            } else if (reference == Float.class) {
                return new JsonPrimitive(primitive.getAsFloat());
            } else if (reference == Double.class) {
                return new JsonPrimitive(primitive.getAsDouble());
            } else if (reference == String.class) {
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

    // Deserializers

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object deserialize(@NotNull References references, @NotNull Context context) {
        @Nullable SerializationProperties properties = context.getProperties();

        // Start deserialization with compatible reference
        for (@NotNull Class<?> reference : references) {
            // Adapters
            {
                @Nullable Adapter adapter = null;

                if (properties != null && properties.getAdapter() != null) {
                    adapter = properties.getAdapter();
                } else if (adapters.map.containsKey(reference)) {
                    adapter = adapters.map.get(reference);
                }

                if (adapter != null) try {
                    return adapter.read(reference, context);
                } catch (@NotNull EOFException e) {
                    // todo: exception message
                    throw new RuntimeException(e);
                }
            }

            if (context.isNullContext()) {
                continue;
            } else if (context.isPrimitiveContext()) {
                @NotNull PrimitiveContext primitive = context.getAsPrimitiveContext();

                if (reference.isEnum() && primitive.isString()) {
                    //noinspection rawtypes
                    return Enum.valueOf((Class) reference, primitive.getAsString());
                } else if (reference == Boolean.class && primitive.isBoolean()) {
                    return primitive.getAsBoolean();
                } else if (reference == Short.class && primitive.isNumber()) {
                    return primitive.getAsShort();
                } else if (reference == Integer.class && primitive.isNumber()) {
                    return primitive.getAsInteger();
                } else if (reference == Long.class && primitive.isNumber()) {
                    return primitive.getAsLong();
                } else if (reference == Float.class && primitive.isNumber()) {
                    return primitive.getAsFloat();
                } else if (reference == Double.class && primitive.isNumber()) {
                    return primitive.getAsDouble();
                } else if (reference == Character.class && primitive.isString()) {
                    return primitive.getAsString().charAt(0);
                } else if (reference == Byte.class && primitive.isNumber()) {
                    return primitive.getAsByte();
                } else if (reference == String.class && primitive.isString()) {
                    return primitive.getAsString();
                } else {
                    throw new UnsupportedOperationException("you cannot use primitive value '" + primitive + "' to parse '" + reference + "'. Is it missing any adapter?");
                }
            }

            // Factory
            @NotNull ContextFactory factory;

            if (properties != null) {
                factory = properties.getContextFactory();
            } else {
                factory = ContextFactory.field();
            }

            // Check if this class should be serialized using Java's Serializer
            if (factory instanceof FieldContextFactory && Classes.usesJavaSerialization(reference)) {
                if (context.isArrayContext()) try {
                    return Classes.javaDeserializeObject(reference, context.getAsArrayContext());
                } catch (@NotNull EOFException e) {
                    throw new RuntimeException("problems trying to deserialize using java native serialization. Is it missing any adapter?", e);
                } catch (@NotNull StreamCorruptedException e) {
                    throw new RuntimeException("stream array corrupted. Is it missing any adapter?", e);
                } catch (@NotNull IOException e) {
                    throw new RuntimeException(e);
                } catch (@NotNull ClassNotFoundException e) {
                    throw new RuntimeException("cannot find class reference", e);
                } else {
                    throw new IllegalStateException("cannot deserialize using java native serialization. Is it missing any adapter?");
                }
            }

            // Deserialize with factory
            try {
                @Nullable Object deserialized = factory.read(reference, this, context);
                return deserialized;
            } catch (@NotNull IOException | @NotNull InstantiationException ignore) {
            }
        }

        if (context.isNullContext()) {
            return null;
        }

        throw new IllegalArgumentException("there's no compatible reference with context '" + context + "'");
    }
    @Override
    public @Nullable Object deserialize(@NotNull References references, @Nullable JsonElement object, @Nullable SerializationProperties properties) throws MalformedClassException {
        return deserialize(references, toContext(object, properties));
    }

    // Contexts

    @Override
    public @NotNull Context toContext(@Nullable Object object, @Nullable SerializationProperties properties) {
        if (object == null) {
            return NullContext.create(properties);
        } else if (object instanceof Context) {
            throw new IllegalArgumentException("you cannot convert a context into a context");
        } else if (object instanceof JsonElement) {
            @NotNull JsonElement element = (JsonElement) object;

            if (element.isJsonNull()) {
                return NullContext.create(properties);
            } else if (element.isJsonPrimitive()) {
                @NotNull JsonPrimitive primitive = element.getAsJsonPrimitive();

                if (primitive.isBoolean()) {
                    return PrimitiveContext.create(primitive.getAsBoolean(), properties);
                } else if (primitive.isNumber()) {
                    return PrimitiveContext.create(primitive.getAsNumber(), properties);
                } else {
                    return PrimitiveContext.create(primitive.getAsString(), properties);
                }
            } else if (element.isJsonObject()) {
                @NotNull MapContext context = MapContext.create(this, properties);
                @NotNull JsonObject json = element.getAsJsonObject();

                for (@NotNull String key : json.keySet()) {
                    @Nullable Context deserialized = toContext(json.get(key));
                    context.setContext(key, deserialized);
                }

                return context;
            } else if (element.isJsonArray()) {
                @NotNull ArrayContext context = ArrayContext.create(this, properties);
                @NotNull JsonArray json = element.getAsJsonArray();

                for (@NotNull JsonElement e : json) {
                    context.write(toContext(e));
                }

                return context;
            } else {
                throw new UnsupportedOperationException("this json element type isn't supported by json serializer '" + element.getClass().getName() + "': " + element);
            }
        } else if (object instanceof Enum<?>) {
            return PrimitiveContext.create(((Enum<?>) object).name().toUpperCase());
        } else if (object instanceof Number) {
            return PrimitiveContext.create((Number) object);
        } else if (object instanceof String) {
            return PrimitiveContext.create((String) object);
        } else if (object instanceof Character) {
            return PrimitiveContext.create((Character) object);
        } else if (object instanceof Boolean) {
            return PrimitiveContext.create((Boolean) object);
        } else {
            @Nullable ContextFactory factory;

            if (properties != null) {
                factory = properties.getContextFactory();
            } else {
                factory = ContextFactory.field();
            }

            return factory.write(object, this, properties);
        }
    }

    // JsonArray

    @Override
    public @NotNull JsonArray serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Serializable object : array) {
            json.add(serialize(object));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Serializable object : iterable) {
            json.add(serialize(object));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Enum<?> @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Enum<?> e : array) {
            json.add(serialize(e));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Boolean b : array) {
            json.add(serialize(b));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (boolean b : array) {
            json.add(serialize(b));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Short s : array) {
            json.add(serialize(s));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (short s : array) {
            json.add(serialize(s));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Integer @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Integer i : array) {
            json.add(serialize(i));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(int @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (int i : array) {
            json.add(serialize(i));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Long l : array) {
            json.add(serialize(l));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (long l : array) {
            json.add(serialize(l));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (float f : array) {
            json.add(serialize(f));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Float f : array) {
            json.add(serialize(f));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Double d : array) {
            json.add(serialize(d));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (double d : array) {
            json.add(serialize(d));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Character @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Character c : array) {
            json.add(serialize(c));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(char @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (char c : array) {
            json.add(serialize(c));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Byte b : array) {
            json.add(serialize(b));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (byte b : array) {
            json.add(serialize(b));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable String @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable String string : array) {
            json.add(serialize(string));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Object @NotNull ... array) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@Nullable Object object : array) {
            json.add(serialize(object));
        }

        return json;
    }
}
