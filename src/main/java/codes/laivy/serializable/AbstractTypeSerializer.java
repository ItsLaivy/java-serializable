package codes.laivy.serializable;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.adapter.provided.CharacterArrayAdapter;
import codes.laivy.serializable.adapter.provided.CollectionAdapter;
import codes.laivy.serializable.adapter.provided.TemporalAdapter;
import codes.laivy.serializable.adapter.provided.UUIDAdapter;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public abstract class AbstractTypeSerializer<T> implements TypeSerializer<T> {

    // Object

    private final @NotNull AdapterMapList adapters = new AdapterMapList();

    public AbstractTypeSerializer() {
        @NotNull Adapter[] adapters = new Adapter[]{
                new TemporalAdapter(),
                new CharacterArrayAdapter(),
                new UUIDAdapter(),
                new CollectionAdapter()
        };

        for (@NotNull Adapter adapter : adapters) {
            for (@NotNull Class<?> reference : adapter.getReferences()) {
                this.adapters.adapterMap.put(reference, adapter);
            }
        }
    }

    // Adapters

    @Override
    public @NotNull Collection<Adapter> getAdapters() {
        return adapters;
    }
    @Override
    public @NotNull Optional<Adapter> getAdapter(@NotNull Class<?> reference) {
        return Optional.ofNullable(adapters.adapterMap.getOrDefault(reference, null));
    }

    // Serializable

    @Override
    public @Nullable T serialize(@Nullable Serializable object) throws MalformedClassException {
        return serialize(object, null);
    }

    @Override
    public @NotNull Iterable<T> serialize(@Nullable Serializable @NotNull ... array) throws MalformedClassException {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Serializable object : array) {
            list.add(serialize(object));
        }

        return list;
    }

    @Override
    public @NotNull Iterable<T> serialize(@NotNull Iterable<@Nullable Serializable> iterable) throws MalformedClassException {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Serializable object : iterable) {
            list.add(serialize(object));
        }

        return list;
    }

    // Enum

    @Override
    public @Nullable T serialize(@Nullable Enum<?> e) {
        return serialize(e, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Enum<?> @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Enum<?> e : array) {
            list.add(serialize(e));
        }

        return list;
    }

    // Boolean

    @Override
    public @NotNull T serialize(boolean b, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Boolean) b, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Boolean b) {
        return serialize(b, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Boolean @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Boolean bool : array) {
            list.add(serialize(bool));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(boolean b) {
        return serialize(b, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(boolean @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (boolean bool : array) {
            list.add(serialize(bool));
        }

        return list;
    }

    // Short

    @Override
    public @NotNull T serialize(short s, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Short) s, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Short s) {
        return serialize(s, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Short @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Short s : array) {
            list.add(serialize(s));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(short s) {
        return serialize(s, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(short @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (short s : array) {
            list.add(serialize(s));
        }

        return list;
    }

    // Integer

    @Override
    public @NotNull T serialize(int i, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Integer) i, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Integer i) {
        return serialize(i, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Integer @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Integer i : array) {
            list.add(serialize(i));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(int i) {
        return serialize(i, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(int @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (int i : array) {
            list.add(serialize(i));
        }

        return list;
    }

    // Long

    @Override
    public @NotNull T serialize(long l, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Long) l, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Long l) {
        return serialize(l, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Long @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Long l : array) {
            list.add(serialize(l));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(long l) {
        return serialize(l, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(long @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (long l : array) {
            list.add(serialize(l));
        }

        return list;
    }

    // Float

    @Override
    public @NotNull T serialize(float f, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Float) f, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Float f) {
        return serialize(f, null);
    }
    @Override
    public @NotNull T serialize(float f) {
        return serialize(f, null);
    }

    @Override
    public @NotNull Iterable<T> serialize(@Nullable Float @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Float f : array) {
            list.add(serialize(f));
        }

        return list;
    }
    @Override
    public @NotNull Iterable<T> serialize(float @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (float f : array) {
            list.add(serialize(f));
        }

        return list;
    }

    // Double

    @Override
    public @NotNull T serialize(double d, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Double) d, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Double d) {
        return serialize(d, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Double @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Double d : array) {
            list.add(serialize(d));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(double d) {
        return serialize(d, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(double @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (double d : array) {
            list.add(serialize(d));
        }

        return list;
    }

    // Character

    @Override
    public @NotNull T serialize(char c, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Character) c, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Character c) {
        return serialize(c, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Character @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Character c : array) {
            list.add(serialize(c));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(char c) {
        return serialize(c, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(char @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (char c : array) {
            list.add(serialize(c));
        }

        return list;
    }

    // Byte

    @Override
    public @NotNull T serialize(byte b, @Nullable SerializationProperties properties) {
        return Objects.requireNonNull(serialize((Byte) b, properties));
    }

    @Override
    public @Nullable T serialize(@Nullable Byte b) {
        return serialize(b, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Byte @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Byte b : array) {
            list.add(serialize(b));
        }

        return list;
    }

    @Override
    public @NotNull T serialize(byte b) {
        return serialize(b, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(byte @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (byte b : array) {
            list.add(serialize(b));
        }

        return list;
    }

    // String

    @Override
    public @Nullable T serialize(@Nullable String string) {
        return serialize(string, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable String @NotNull ... array) {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable String string : array) {
            list.add(serialize(string));
        }

        return list;
    }

    // Pure

    @Override
    public @Nullable T serialize(@Nullable Object object) throws MalformedClassException {
        return serialize(object, null);
    }
    @Override
    public @NotNull Iterable<T> serialize(@Nullable Object @NotNull ... array) throws MalformedClassException {
        @NotNull List<T> list = new LinkedList<>();

        for (@Nullable Object object : array) {
            list.add(serialize(object));
        }

        return list;
    }

    // Redirect to the #serialize(Object, SerializationProperties) method

    @Override
    public @Nullable T serialize(@Nullable Serializable object, @Nullable SerializationProperties properties) throws MalformedClassException {
        return serialize((Object) object, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Enum<?> e, @Nullable SerializationProperties properties) {
        return serialize((Object) e, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Boolean b, @Nullable SerializationProperties properties) {
        return serialize((Object) b, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Short s, @Nullable SerializationProperties properties) {
        return serialize((Object) s, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Integer i, @Nullable SerializationProperties properties) {
        return serialize((Object) i, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Long l, @Nullable SerializationProperties properties) {
        return serialize((Object) l, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Float f, @Nullable SerializationProperties properties) {
        return serialize((Object) f, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Double d, @Nullable SerializationProperties properties) {
        return serialize((Object) d, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Character c, @Nullable SerializationProperties properties) {
        return serialize((Object) c, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable Byte b, @Nullable SerializationProperties properties) {
        return serialize((Object) b, properties);
    }
    @Override
    public @Nullable T serialize(@Nullable String string, @Nullable SerializationProperties properties) {
        return serialize((Object) string, properties);
    }

    // Deserialization redirects

    @Override
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @Nullable T @NotNull [] array) throws MalformedClassException {
        @NotNull List<E> list = new LinkedList<>();
        @NotNull References references = References.of(reference);

        for (@Nullable T serialized : array) {
            //noinspection unchecked
            list.add((E) deserialize(references, serialized));
        }

        return list;
    }
    @Override
    public @NotNull <E> Iterable<@Nullable E> deserialize(@NotNull Class<E> reference, @NotNull Iterable<@Nullable T> array) throws MalformedClassException {
        @NotNull List<E> list = new LinkedList<>();
        @NotNull References references = References.of(reference);

        for (@Nullable T serialized : array) {
            //noinspection unchecked
            list.add((E) deserialize(references, serialized));
        }

        return list;
    }

    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @Nullable T element) throws MalformedClassException {
        @Nullable Object object = deserialize(References.of(reference), element, null);

        if (object != null && !reference.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("unexpected object reference '" + object.getClass().getName() + "', expected '" + reference.getName() + "': " + object);
        }

        //noinspection unchecked
        return (E) object;
    }
    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context, @Nullable SerializationProperties properties) {
        @Nullable Object object = deserialize(References.of(reference), context, properties);

        if (object != null && !reference.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("unexpected object reference '" + object.getClass().getName() + "', expected '" + reference.getName() + "': " + object);
        }

        //noinspection unchecked
        return (E) object;
    }
    @Override
    public <E> @Nullable E deserialize(@NotNull Class<E> reference, @NotNull Context context) {
        @Nullable Object object = deserialize(References.of(reference), context, null);

        if (object != null && !reference.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("unexpected object reference '" + object.getClass().getName() + "', expected '" + reference.getName() + "': " + object);
        }

        //noinspection unchecked
        return (E) object;
    }

    @Override
    public @Nullable Object deserialize(@NotNull References references, @Nullable T object) throws MalformedClassException {
        return deserialize(references, object, null);
    }
    @Override
    public @Nullable Object deserialize(@NotNull References references, @NotNull Context context) {
        return deserialize(references, context, null);
    }

    // Contexts

    @Override
    public @NotNull Context toContext(@Nullable Object object) {
        return toContext(object, null);
    }

    // Classes

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

            for (@NotNull Map.Entry<Class<?>, Adapter> entry : new HashMap<>(adapterMap).entrySet()) {
                if (entry.getValue() == adapter) {
                    adapterMap.remove(entry.getKey());
                }
            }

            return adapter;
        }
    }

}
