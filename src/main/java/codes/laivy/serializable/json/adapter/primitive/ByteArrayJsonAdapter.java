package codes.laivy.serializable.json.adapter.primitive;

import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

final class ByteArrayJsonAdapter {

    private ByteArrayJsonAdapter() {
        throw new UnsupportedOperationException("this class cannot be instantiated!");
    }

    // Classes

    public static class Wrapper implements JsonAdapter<Byte[]> {

        public Wrapper() {
        }

        @Override
        public @NotNull Class<Byte[]> getReference() {
            return Byte[].class;
        }

        @Override
        public @Nullable JsonElement serialize(@Nullable Byte @Nullable [] array) throws InvalidClassException {
            if (array == null) {
                return null;
            }

            boolean anyNull = Arrays.stream(array).anyMatch(Objects::isNull);

            if (anyNull) { // There's an impostor here...
                @NotNull JsonArray json = new JsonArray();

                for (@Nullable Byte c : array) {
                    if (c == null) json.add(JsonNull.INSTANCE);
                    else json.add(c);
                }

                return json;
            } else { // All clean to be a plain text!
                byte[] primitive = new byte[array.length];
                for (int row = 0; row < array.length; row++) {
                    @NotNull Byte b = Objects.requireNonNull(array[row]);
                    primitive[row] = b;
                }

                return new JsonPrimitive(Base64.getEncoder().encodeToString(primitive));
            }
        }

        @Override
        public Byte @Nullable [] deserialize(@NotNull Class<Byte[]> reference, @Nullable JsonElement object) throws InvalidClassException {
            if (object == null) {
                return null;
            }

            if (object.isJsonPrimitive()) {
                byte[] original = Base64.getDecoder().decode(object.getAsJsonPrimitive().getAsString());
                @NotNull Byte[] array = new Byte[original.length];

                for (int i = 0; i < original.length; i++) {
                    array[i] = original[i];
                }

                return array;
            } else if (object.isJsonArray()) {
                @NotNull JsonArray array = object.getAsJsonArray();
                @Nullable Byte[] chars = new Byte[array.size()];

                int row = 0;
                for (@NotNull JsonElement element : array) {
                    chars[row] = element.isJsonNull() ? null : element.getAsByte();
                    row++;
                }

                return chars;
            } else {
                throw new IllegalArgumentException("cannot parse this json element into a valid byte array using character adapter");
            }
        }
    }
    public static class Primitive implements JsonAdapter<byte[]> {

        public Primitive() {
        }

        @Override
        public @NotNull Class<byte[]> getReference() {
            return byte[].class;
        }

        @Override
        public @Nullable JsonElement serialize(byte @Nullable [] object) throws InvalidClassException {
            if (object == null) {
                return null;
            }

            return new JsonPrimitive(Base64.getEncoder().encodeToString(object));
        }

        @Override
        public byte @Nullable [] deserialize(@NotNull Class<byte[]> reference, @Nullable JsonElement object) throws InvalidClassException {
            if (object == null) {
                return null;
            }

            if (object.isJsonPrimitive()) {
                return Base64.getDecoder().decode(object.getAsJsonPrimitive().getAsString());
            } else if (object.isJsonArray()) {
                @NotNull JsonArray array = object.getAsJsonArray();
                byte[] chars = new byte[array.size()];

                int row = 0;
                for (@NotNull JsonElement element : array) {
                    chars[row] = element.getAsByte();
                    row++;
                }

                return chars;
            } else {
                throw new IllegalArgumentException("cannot parse this json element into a valid byte array using character adapter");
            }
        }

    }

}
