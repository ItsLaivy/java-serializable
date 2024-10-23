package codes.laivy.serializable.context;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
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

    private final @Nullable SerializationProperties properties;

    private final @NotNull LinkedList<Context> contexts = new LinkedList<>();

    public ArrayContextImpl(@NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        this.serializer = serializer;
        this.properties = properties;
    }

    // Modules

    @Override
    public @NotNull Serializer getSerializer() {
        return serializer;
    }

    @Override
    public @Nullable Object readObject(@NotNull References references) throws EOFException {
        return serializer.deserialize(references, poll());
    }
    @Override
    public @NotNull Context readContext() throws EOFException {
        return poll();
    }

    @Override
    public boolean readBoolean() throws EOFException {
        return poll().getAsPrimitiveContext().getAsBoolean();
    }
    @Override
    public byte readByte() throws EOFException {
        return poll().getAsPrimitiveContext().getAsByte();
    }
    @Override
    public short readShort() throws EOFException {
        return poll().getAsPrimitiveContext().getAsShort();
    }
    @Override
    public char readChar() throws EOFException {
        return poll().getAsPrimitiveContext().getAsString().charAt(0);
    }
    @Override
    public int readInt() throws EOFException {
        return poll().getAsPrimitiveContext().getAsInteger();
    }
    @Override
    public long readLong() throws EOFException {
        return poll().getAsPrimitiveContext().getAsLong();
    }
    @Override
    public float readFloat() throws EOFException {
        return poll().getAsPrimitiveContext().getAsFloat();
    }
    @Override
    public double readDouble() throws EOFException {
        return poll().getAsPrimitiveContext().getAsDouble();
    }
    @Override
    public @NotNull String readString() throws EOFException {
        return poll().getAsPrimitiveContext().getAsString();
    }

    @Override
    public void write(@Nullable Object object) {
        add(serializer.toContext(object, properties));
    }
    @Override
    public void write(@NotNull Context context) {
        add(context);
    }

    // Array

    private @NotNull Context poll() throws EOFException {
        synchronized (lock) {
            if (contexts.isEmpty()) {
                throw new EOFException();
            }

            return contexts.poll();
        }
    }

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

    // Properties

    @Override
    public @Nullable SerializationProperties getProperties() {
        return properties;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof ArrayContextImpl)) return false;
        @NotNull ArrayContextImpl contexts1 = (ArrayContextImpl) object;
        return Objects.equals(getSerializer(), contexts1.getSerializer()) && Objects.equals(getProperties(), contexts1.getProperties()) && Objects.equals(contexts, contexts1.contexts);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getSerializer(), getProperties(), contexts);
    }

    @Override
    public @NotNull String toString() {
        return "ArrayContextImpl{" +
                "serializer=" + serializer +
                ", properties=" + properties +
                ", contexts=" + contexts +
                '}';
    }

}
