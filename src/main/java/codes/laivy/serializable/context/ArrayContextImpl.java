package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.serializers.MethodSerialization;
import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

@MethodSerialization
final class ArrayContextImpl implements ArrayContext {

    // Object

    private final @NotNull Object lock = new Object();
    private final @NotNull Serializer serializer;

    private final @NotNull LinkedList<Context> list;

    public ArrayContextImpl(@NotNull Serializer serializer) {
        this.serializer = serializer;
        this.list = new LinkedList<>();
    }
    public ArrayContextImpl(@NotNull LinkedList<Context> list) {
        this.serializer = JsonSerializer.getInstance();
        this.list = list;
    }

    // Modules

    @Override
    public @NotNull Serializer getSerializer() {
        return serializer;
    }

    // Writers

    @Override
    public @NotNull Context readContext() throws EOFException {
        synchronized (lock) {
            if (list.isEmpty()) {
                throw new EOFException();
            }

            return list.poll();
        }
    }
    @Override
    public void write(@NotNull Context context) {
        add(context);
    }

    // Array

    @Override
    public int size() {
        return list.size();
    }
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public @NotNull Iterator<Context> iterator() {
        return list.iterator();
    }

    @Override
    public @Nullable Object @NotNull [] toArray() {
        return list.toArray();
    }
    @Override
    public <T> @NotNull T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(@NotNull Context context) {
        return list.add(context);
    }
    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }
    @Override
    public boolean addAll(@NotNull Collection<? extends Context> c) {
        return list.addAll(c);
    }
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return list.removeAll(c);
    }
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    // Serializers

    private static @NotNull LinkedList<Context> serialize(@NotNull ArrayContextImpl context) {
        return context.list;
    }
    private static @NotNull ArrayContextImpl deserialize(@NotNull LinkedList<Context> list) {
        return new ArrayContextImpl(list);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof ArrayContextImpl)) return false;
        @NotNull ArrayContextImpl contexts1 = (ArrayContextImpl) object;
        return Objects.equals(getSerializer(), contexts1.getSerializer()) && Objects.equals(list, contexts1.list);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getSerializer(), list);
    }

    @Override
    public @NotNull String toString() {
        return list.toString();
    }

}
