package codes.laivy.serializable.json;

import codes.laivy.serializable.TypeSerializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.adapter.provided.CharacterArrayAdapter;
import codes.laivy.serializable.adapter.provided.CollectionAdapter;
import codes.laivy.serializable.adapter.provided.TemporalAdapter;
import codes.laivy.serializable.adapter.provided.UUIDAdapter;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.factory.context.ContextFactory;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

// todo: gson adapter
public class JsonSerializer implements TypeSerializer<JsonElement> {

    // Static initializers

    @SuppressWarnings("FieldMayBeFinal")
    private static @NotNull JsonSerializer instance = new JsonSerializer();

    public static @NotNull JsonSerializer getInstance() {
        return instance;
    }

    // Object

    private final @NotNull AdapterMapList adapterMapList = new AdapterMapList();

    public JsonSerializer() {
        @NotNull Adapter[] adapters = new Adapter[]{
                new TemporalAdapter(),
                new CharacterArrayAdapter(),
                new UUIDAdapter(),
                new CollectionAdapter()
        };

        for (@NotNull Adapter adapter : adapters) {
            for (@NotNull Class<?> reference : adapter.getReferences()) {
                adapterMapList.adapterMap.put(reference, adapter);
            }
        }
    }

    // Getters

    @Override
    public @NotNull Collection<Adapter> getAdapters() {
        return adapterMapList;
    }
    @Override
    public @NotNull Optional<Adapter> getAdapter(@NotNull Class<?> reference) {
        return Optional.ofNullable(adapterMapList.adapterMap.getOrDefault(reference, null));
    }

    // Serializers and Deserializers

    @Override
    public @NotNull JsonElement serialize(@Nullable Serializable object) throws MalformedClassException {
        return this.serialize((Object) object);
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Object object) throws MalformedClassException {
        if (object == null) {
            return JsonNull.INSTANCE;
        }

        if (object instanceof Context) {
            return serialize((Context) object);
        }

        return serialize(object, null);
    }

    @Override
    public @NotNull JsonElement serialize(@Nullable Object object, @Nullable SerializationProperties properties) {
        // Check nullability
        if (object == null) {
            return JsonNull.INSTANCE;
        }

        // Context factories
        @NotNull ContextFactory factory;

        if (properties == null) {
            factory = ContextFactory.field();
        } else {
            factory = properties.getContextFactory();
        }

        // Serialize
        return serialize(factory.write(object, this, properties));
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Object @NotNull ... array) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Object object : array) {
            json.add(serialize(object));
        }

        return json;
    }

    @Override
    public @NotNull JsonArray serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Serializable serializable : array) {
            json.add(serialize(serializable));
        }

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Serializable serializable : iterable) {
            json.add(serialize(serializable));
        }

        return json;
    }

    @Override
    public @NotNull JsonElement serialize(@Nullable Enum<?> e) {
        return e != null ? new JsonPrimitive(e.name()) : JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Enum<?> @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Enum<?> e : array) {
            if (e != null) json.add(new JsonPrimitive(e.name()));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Boolean b) {
        return b != null ? new JsonPrimitive(b): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Boolean b : array) {
            if (b != null) json.add(new JsonPrimitive(b));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Short s) {
        return s != null ? new JsonPrimitive(s): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Short s : array) {
            if (s != null) json.add(new JsonPrimitive(s));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Integer i) {
        return i != null ? new JsonPrimitive(i): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Integer @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Integer i : array) {
            if (i != null) json.add(new JsonPrimitive(i));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Long l) {
        return l != null ? new JsonPrimitive(l): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Long l : array) {
            if (l != null) json.add(new JsonPrimitive(l));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Float f) {
        return f != null ? new JsonPrimitive(f): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Float f : array) {
            if (f != null) json.add(new JsonPrimitive(f));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Double d) {
        return d != null ? new JsonPrimitive(d): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Double d : array) {
            if (d != null) json.add(new JsonPrimitive(d));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Character c) {
        return c != null ? new JsonPrimitive(c): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Character @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Character c : array) {
            if (c != null) json.add(new JsonPrimitive(c));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable Byte b) {
        return b != null ? new JsonPrimitive(b): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable Byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable Byte b : array) {
            if (b != null) json.add(new JsonPrimitive(b));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }
    @Override
    public @NotNull JsonElement serialize(@Nullable String string) {
        return string != null ? new JsonPrimitive(string): JsonNull.INSTANCE;
    }
    @Override
    public @NotNull JsonArray serialize(@Nullable String @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (@Nullable String string : array) {
            if (string != null) json.add(new JsonPrimitive(string));
            else json.add(JsonNull.INSTANCE);
        }

        return json;
    }

    @Override
    public @NotNull JsonElement serialize(boolean b) {
        return new JsonPrimitive(b);
    }
    @Override
    public @NotNull JsonElement serialize(char c) {
        return new JsonPrimitive(c);
    }
    @Override
    public @NotNull JsonElement serialize(byte b) {
        return new JsonPrimitive(b);
    }
    @Override
    public @NotNull JsonElement serialize(short s) {
        return new JsonPrimitive(s);
    }
    @Override
    public @NotNull JsonElement serialize(int i) {
        return new JsonPrimitive(i);
    }
    @Override
    public @NotNull JsonElement serialize(long l) {
        return new JsonPrimitive(l);
    }
    @Override
    public @NotNull JsonElement serialize(float f) {
        return new JsonPrimitive(f);
    }
    @Override
    public @NotNull JsonElement serialize(double d) {
        return new JsonPrimitive(d);
    }

    @Override
    public @NotNull JsonArray serialize(boolean @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (boolean b : array) json.add(new JsonPrimitive(b));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(char @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (char c : array) json.add(new JsonPrimitive(c));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(byte @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (byte b : array) json.add(new JsonPrimitive(b));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(short @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (short s : array) json.add(new JsonPrimitive(s));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(int @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (int i : array) json.add(new JsonPrimitive(i));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(long @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (long l : array) json.add(new JsonPrimitive(l));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(float @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (float f : array) json.add(new JsonPrimitive(f));

        return json;
    }
    @Override
    public @NotNull JsonArray serialize(double @NotNull ... array) {
        @NotNull JsonArray json = new JsonArray();
        for (double d : array) json.add(new JsonPrimitive(d));

        return json;
    }

    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable JsonElement element) throws MalformedClassException {
        return deserialize(reference, element, null);
    }

    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable JsonElement element, @Nullable SerializationProperties properties) throws MalformedClassException {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        return deserialize(reference, toContext(element, properties));
    }

    @Override
    public @Nullable Object deserialize(@NotNull References references, @NotNull Context context, @Nullable SerializationProperties properties) {
        return null;
    }
    @Override
    public @Nullable Object deserialize(@NotNull References references, @Nullable JsonElement object, @Nullable SerializationProperties properties) throws MalformedClassException {
        return null;
    }

    @Override
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable JsonElement @NotNull [] array) throws MalformedClassException {
        return deserialize(reference, array, null);
    }
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull JsonArray array) throws MalformedClassException {
        @NotNull List<E> list = new LinkedList<>();

        for (@Nullable JsonElement element : array) {
            list.add(deserialize(reference, element));
        }

        return list;
    }

    @Override
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable JsonElement @NotNull [] array, @Nullable SerializationProperties properties) throws MalformedClassException {
        @NotNull List<E> list = new LinkedList<>();

        for (@Nullable JsonElement element : array) {
            list.add(deserialize(reference, element, properties));
        }

        return list;
    }

    // Context

    @Override
    public @NotNull JsonElement serialize(@NotNull Context context) {
        if (context.isNullContext()) {
            return JsonNull.INSTANCE;
        } else if (context instanceof PrimitiveContext) {
            @NotNull PrimitiveContext primitive = (PrimitiveContext) context;

            if (primitive.isString()) {
                return serialize(primitive.getAsString());
            } else if (primitive.isBoolean()) {
                return serialize(primitive.getAsBoolean());
            } else if (primitive.isNumber()) {
                return serialize(primitive.getAsNumber());
            } else if (primitive.isCharacter()) {
                return serialize(primitive.getAsCharacter());
            } else {
                return serialize(primitive.getAsString());
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

    @Override
    public @NotNull Context toContext(@Nullable Object object, @Nullable SerializationProperties properties) {
        if (object == null) {
            return NullContext.create(properties);
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
        } else if (object instanceof Context) {
            return (Context) object;
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

    // Classes

    private static final class AdapterMapList extends AbstractList<Adapter> {

        final @NotNull Map<Class<?>, Adapter> adapterMap = new HashMap<>();

        @Override
        public @NotNull Adapter get(int index) {
            return adapterMap.values().stream().skip(index).findFirst().orElseThrow(IndexOutOfBoundsException::new);
        }
        @Override
        public int size() {
            return adapterMap.size();
        }

        @Override
        public void add(int index, @Nullable Adapter adapter) {
            if (adapter == null) {
                throw new IllegalArgumentException("cannot add a null adapter");
            }

            for (@NotNull Class<?> reference : adapter.getReferences()) {
                adapterMap.put(reference, adapter);
            }
        }
        @Override
        public @NotNull Adapter remove(int index) {
            @NotNull Adapter adapter = get(index);

            for (@NotNull Entry<Class<?>, Adapter> entry : new HashMap<>(adapterMap).entrySet()) {
                if (entry.getValue() == adapter) {
                    adapterMap.remove(entry.getKey());
                }
            }

            return adapter;
        }
    }

    // Utilities

}
