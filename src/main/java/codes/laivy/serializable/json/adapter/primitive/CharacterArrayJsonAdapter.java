package codes.laivy.serializable.json.adapter.primitive;

import codes.laivy.serializable.json.JsonSerializable;
import codes.laivy.serializable.json.adapter.JsonAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.Objects;

final class CharacterArrayJsonAdapter {

    private CharacterArrayJsonAdapter() {
        throw new UnsupportedOperationException("this class cannot be instantiated!");
    }

    // Classes

    public static class Wrapper implements JsonAdapter<Character[]> {

        public Wrapper() {
        }

        @Override
        public @NotNull Class<Character[]> getReference() {
            return Character[].class;
        }

        @Override
        public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, @Nullable Character @Nullable [] instance) throws InvalidClassException {
            if (instance == null) {
                return null;
            }

            boolean anyNull = Arrays.stream(instance).anyMatch(Objects::isNull);

            if (anyNull) { // There's an impostor here...
                @NotNull JsonArray json = new JsonArray();

                for (@Nullable Character c : instance) {
                    if (c == null) json.add(JsonNull.INSTANCE);
                    else json.add(c);
                }

                return json;
            } else { // All clean to be a plain text!
                @NotNull StringBuilder builder = new StringBuilder();

                for (@UnknownNullability Character c : instance) {
                    builder.append(c.charValue());
                }

                return new JsonPrimitive(builder.toString());
            }
        }

        @Override
        public Character @Nullable [] deserialize(@NotNull JsonSerializable serializer, @NotNull Class<Character[]> reference, @Nullable JsonElement object) throws InvalidClassException {
            if (object == null) {
                return null;
            }

            if (object.isJsonPrimitive()) {
                char[] original = object.getAsJsonPrimitive().getAsString().toCharArray();
                @NotNull Character[] array = new Character[original.length];

                for (int i = 0; i < original.length; i++) {
                    array[i] = original[i];
                }

                return array;
            } else if (object.isJsonArray()) {
                @NotNull JsonArray array = object.getAsJsonArray();
                @Nullable Character[] chars = new Character[array.size()];

                int row = 0;
                for (@NotNull JsonElement element : array) {
                    chars[row] = element.isJsonNull() ? null : element.getAsString().charAt(row);
                    row++;
                }

                return chars;
            } else {
                throw new IllegalArgumentException("cannot parse this json element into a valid char array using character adapter");
            }
        }
    }
    public static class Primitive implements JsonAdapter<char[]> {

        public Primitive() {
        }

        @Override
        public @NotNull Class<char[]> getReference() {
            return char[].class;
        }

        @Override
        public @Nullable JsonElement serialize(@NotNull JsonSerializable serializer, char @Nullable [] instance) throws InvalidClassException {
            if (instance == null) {
                return null;
            }

            return new JsonPrimitive(new String(instance));
        }

        @Override
        public char @Nullable [] deserialize(@NotNull JsonSerializable serializer, @NotNull Class<char[]> reference, @Nullable JsonElement object) throws InvalidClassException {
            if (object == null) {
                return null;
            }

            if (object.isJsonPrimitive()) {
                return object.getAsJsonPrimitive().getAsString().toCharArray();
            } else if (object.isJsonArray()) {
                @NotNull JsonArray array = object.getAsJsonArray();
                char[] chars = new char[array.size()];

                int row = 0;
                for (@NotNull JsonElement element : array) {
                    chars[row] = element.getAsString().charAt(row);
                    row++;
                }

                return chars;
            } else {
                throw new IllegalArgumentException("cannot parse this json element into a valid char array using character adapter");
            }
        }

    }

}
