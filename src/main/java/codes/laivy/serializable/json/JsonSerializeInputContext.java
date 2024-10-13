package codes.laivy.serializable.json;

import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.exception.NullConcreteClassException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.EOFException;
import java.lang.reflect.AnnotatedType;
import java.util.*;

import static codes.laivy.serializable.json.SerializingProcess.checkCompatible;
import static codes.laivy.serializable.utilities.Classes.isConcrete;

final class JsonSerializeInputContext implements SerializeInputContext {

    private final @NotNull JsonSerializer serializer;

    private final @NotNull Class<?> reference;
    private final @NotNull Object lock = new Object();

    private int index = 0;
    private final @NotNull List<@Nullable JsonElement> objects = new LinkedList<>();
    private final @NotNull Map<String, @Nullable JsonElement> fields = new LinkedHashMap<>();
    private final @Nullable AnnotatedType annotatedType;
    
    public JsonSerializeInputContext(@NotNull JsonSerializer serializer, @NotNull Class<?> reference, @NotNull JsonElement element, @Nullable AnnotatedType annotatedType) {
        this.reference = reference;
        this.serializer = serializer;
        this.annotatedType = annotatedType;

        if (element.isJsonObject()) {
            @NotNull JsonObject object = element.getAsJsonObject();

            for (@NotNull String name : object.keySet()) {
                this.fields.compute(name, (k, value) -> value);
            }
        } else if (element.isJsonArray()) {
            for (@NotNull JsonElement data : element.getAsJsonArray()) {
                if (data.isJsonObject()) {
                    @NotNull JsonObject object = data.getAsJsonObject();

                    for (@NotNull String name : object.keySet()) {
                        @NotNull JsonElement value =  object.get(name);

                        if (name.startsWith("!")) {
                            this.fields.put(name, value);
                        } else {
                            this.objects.add(data);
                        }
                    }
                } else if (data.isJsonPrimitive()) {
                    objects.add(data.getAsJsonPrimitive());
                } else if (data.isJsonNull()) {
                    objects.add(null);
                } else {
                    throw new IllegalArgumentException("invalid element data '" + data + "' (json object or primitive expected)");
                }
            }
        } else if (element.isJsonPrimitive()) {
            objects.add(element.getAsJsonPrimitive());
        }
    }

    // Getters

    @Override
    public @NotNull Class<?> getReference() {
        return reference;
    }

    // Modules

    @Override
    public boolean readBoolean() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsBoolean();
    }

    @Override
    public byte readByte() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsByte();
    }

    @Override
    public short readShort() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsShort();
    }

    @Override
    public char readChar() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsString().charAt(0);
    }

    @Override
    public int readInt() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsInt();
    }

    @Override
    public long readLong() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsLong();
    }

    @Override
    public float readFloat() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsFloat();
    }

    @Override
    public double readDouble() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            throw new NullPointerException();
        }

        return element.getAsDouble();
    }

    @Override
    public @Nullable String readLine() throws EOFException {
        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            return null;
        }

        return element.getAsString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> @Nullable E readObject(@NotNull Class<E> reference) throws EOFException {
        if (!isConcrete(reference)) {
            throw new NullConcreteClassException("the reference class '" + reference + "' isn't concrete");
        }

        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
            index++;
        }

        if (element == null) {
            return null;
        }

        @NotNull SerializingProcess process = new SerializingProcess(getSerializer(), reference, annotatedType);
        return (E) process.deserialize(element);
    }

    @Override
    public @UnknownNullability Object readObject(@NotNull Class<?> @NotNull [] references) throws EOFException {
        if (references.length == 0) {
            throw new IllegalStateException("the references array must not be empty");
        }

        @Nullable JsonElement element;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            element = objects.get(index);
        }

        if (element == null) {
            return null;
        }

        for (@NotNull Class<?> reference : references) {
            if (!isConcrete(reference)) {
                throw new NullConcreteClassException("the reference class '" + reference + "' isn't concrete");
            } else if (checkCompatible(serializer, null, reference, element, annotatedType)) {
                index++;

                @NotNull SerializingProcess process = new SerializingProcess(getSerializer(), reference, annotatedType);
                return process.deserialize(element);
            }
        }

        throw new IllegalArgumentException("cannot deserialize object with the references references '" + Arrays.toString(references) + "'");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> @Nullable E readField(@NotNull Class<E> reference, @NotNull String name) {
        if (fields.containsKey(name)) {
            throw new IllegalArgumentException("there's no field named '" + name + "' at this context");
        } else if (!isConcrete(reference)) {
            throw new NullConcreteClassException("the reference class '" + reference + "' isn't concrete");
        }

        @NotNull SerializingProcess process = new SerializingProcess(getSerializer(), reference, annotatedType);
        return (E) process.deserialize(fields.get(name));
    }
    @Override
    public @NotNull String @NotNull [] getFields() {
        return fields.keySet().toArray(new String[0]);
    }

    @Override
    public @Nullable AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public @NotNull JsonSerializer getSerializer() {
        return serializer;
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return "JsonSerializeInputContext{" +
                "reference=" + reference +
                ", index=" + index +
                ", objects=" + objects +
                ", fields=" + fields +
                '}';
    }

}
