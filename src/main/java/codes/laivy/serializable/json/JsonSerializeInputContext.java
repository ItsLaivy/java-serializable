package codes.laivy.serializable.json;

import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.exception.NullConcreteClassException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static codes.laivy.serializable.json.SerializingProcess.isConcrete;

final class JsonSerializeInputContext<T> implements SerializeInputContext<T> {

    private final @NotNull JsonSerializer serializer;

    private final @NotNull Class<T> reference;
    private final @NotNull Object lock = new Object();

    private int index = 0;
    private final @NotNull List<@Nullable JsonPrimitive> objects = new LinkedList<>();
    private final @NotNull Map<String, @Nullable JsonElement> fields = new LinkedHashMap<>();

    public JsonSerializeInputContext(@NotNull JsonSerializer serializer, @NotNull Class<T> reference) {
        this.reference = reference;
        this.serializer = serializer;
    }
    public JsonSerializeInputContext(@NotNull JsonSerializer serializer, @NotNull Class<T> reference, @NotNull JsonElement element) {
        this.reference = reference;
        this.serializer = serializer;

        if (element.isJsonObject()) {
            @NotNull JsonObject object = element.getAsJsonObject();

            for (@NotNull String name : object.keySet()) {
                this.fields.compute(name, (k, value) -> value);
            }
        } else if (element.isJsonArray()) {
            for (@NotNull JsonElement data : element.getAsJsonArray()) {
                if (data.isJsonObject()) {
                    if (!fields.isEmpty()) {
                        throw new IllegalArgumentException("invalid element data '" + element + "' (multiples json objects)");
                    }

                    @NotNull JsonObject fields = data.getAsJsonObject();

                    for (@NotNull String name : fields.keySet()) {
                        @NotNull JsonElement value =  fields.get(name);
                        this.fields.put(name, value);
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
    public @NotNull Class<T> getReference() {
        return reference;
    }

    // Modules

    @Override
    public boolean readBoolean() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsBoolean();
    }

    @Override
    public byte readByte() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsByte();
    }

    @Override
    public short readShort() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsShort();
    }

    @Override
    public char readChar() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsString().charAt(0);
    }

    @Override
    public int readInt() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsInt();
    }

    @Override
    public long readLong() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsLong();
    }

    @Override
    public float readFloat() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsFloat();
    }

    @Override
    public double readDouble() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            throw new NullPointerException();
        }

        return primitive.getAsDouble();
    }

    @Override
    public @Nullable String readLine() throws EOFException {
        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            return null;
        }

        return primitive.getAsString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> @Nullable E readObject(@NotNull Class<E> reference) throws EOFException {
        if (!isConcrete(reference)) {
            throw new NullConcreteClassException("the reference class '" + reference + "' isn't concrete");
        }

        @Nullable JsonPrimitive primitive;
        synchronized (lock) {
            if (index >= objects.size()) throw new EOFException();
            primitive = objects.get(index);
            index++;
        }

        if (primitive == null) {
            return null;
        }

        @NotNull SerializingProcess process = new SerializingProcess(getSerializer(), reference);
        return (E) process.deserialize(primitive);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> @Nullable E readField(@NotNull Class<E> reference, @NotNull String name) {
        if (fields.containsKey(name)) {
            throw new IllegalArgumentException("there's no field named '" + name + "' at this context");
        } else if (!isConcrete(reference)) {
            throw new NullConcreteClassException("the reference class '" + reference + "' isn't concrete");
        }

        @NotNull SerializingProcess process = new SerializingProcess(getSerializer(), reference);
        return (E) process.deserialize(fields.get(name));
    }
    @Override
    public @NotNull String @NotNull [] getFields() {
        return fields.keySet().toArray(new String[0]);
    }

    @Override
    public @NotNull JsonSerializer getSerializer() {
        return serializer;
    }

}
