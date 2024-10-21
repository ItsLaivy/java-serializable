package codes.laivy.serializable.reference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

final class ReferencesImpl implements References {

    private final @NotNull Set<Class<?>> classes = new LinkedHashSet<>();

    ReferencesImpl() {
    }

    // Modules

    @Override
    public int size() {
        return classes.size();
    }
    @Override
    public boolean isEmpty() {
        return classes.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return classes.contains(o);
    }

    @Override
    public @NotNull Iterator<Class<?>> iterator() {
        return classes.iterator();
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return classes.toArray();
    }

    @Override
    public <T> @NotNull T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return classes.toArray(a);
    }

    @Override
    public boolean add(@NotNull Class<?> reference) {
        return classes.add(reference);
    }
    @Override
    public boolean remove(@NotNull Object o) {
        return classes.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return classes.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Class<?>> c) {
        return classes.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return classes.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return classes.retainAll(c);
    }

    @Override
    public void clear() {
        classes.clear();
    }

}
