package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

final class ArrayContextImpl implements ArrayContext {

    // Object

    private final @NotNull Object lock = new Object();
    private final @NotNull Serializer serializer;

    private final @NotNull LinkedList<Context> contexts = new LinkedList<>();

    public ArrayContextImpl(@NotNull Serializer serializer) {
        this.serializer = serializer;
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
            if (contexts.isEmpty()) {
                throw new EOFException();
            }

            return contexts.poll();
        }
    }
    @Override
    public void write(@NotNull Context context) {
        add(context);
    }

    // Array

    @Override
    public int size() {
        return contexts.size();
    }
    @Override
    public boolean isEmpty() {
        return contexts.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return contexts.contains(o);
    }

    @Override
    public @NotNull Iterator<Context> iterator() {
        return contexts.iterator();
    }

    @Override
    public @Nullable Object @NotNull [] toArray() {
        return contexts.toArray();
    }
    @Override
    public <T> @NotNull T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return contexts.toArray(a);
    }

    @Override
    public boolean add(@NotNull Context context) {
        return contexts.add(context);
    }
    @Override
    public boolean remove(Object o) {
        return contexts.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return contexts.containsAll(c);
    }
    @Override
    public boolean addAll(@NotNull Collection<? extends Context> c) {
        return contexts.addAll(c);
    }
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return contexts.removeAll(c);
    }
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return contexts.retainAll(c);
    }

    @Override
    public void clear() {
        contexts.clear();
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof ArrayContextImpl)) return false;
        @NotNull ArrayContextImpl contexts1 = (ArrayContextImpl) object;
        return Objects.equals(getSerializer(), contexts1.getSerializer()) && Objects.equals(contexts, contexts1.contexts);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getSerializer(), contexts);
    }

    @Override
    public @NotNull String toString() {
        return "ArrayContextImpl{" +
                "serializer=" + serializer +
                ", contexts=" + contexts +
                '}';
    }

}
