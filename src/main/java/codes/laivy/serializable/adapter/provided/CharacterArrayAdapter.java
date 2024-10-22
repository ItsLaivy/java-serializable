package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.ArrayContext;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.PrimitiveContext;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.EOFException;
import java.util.Arrays;
import java.util.Objects;

public class CharacterArrayAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] {
                Character[].class,
                char[].class
        };
    }

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        if (object instanceof Character[]) {
            @Nullable Character[] instance = (Character[]) object;

            boolean anyNull = Arrays.stream(instance).anyMatch(Objects::isNull);

            if (anyNull) { // There's an impostor here...
                @NotNull ArrayContext context = ArrayContext.create(serializer);

                for (@Nullable Character c : instance) {
                    context.write(c);
                }

                return context;
            } else { // All clean to be a plain text!
                @NotNull StringBuilder builder = new StringBuilder();
                for (@UnknownNullability Character c : instance) {
                    builder.append(c.charValue());
                }

                return PrimitiveContext.create(builder.toString());
            }
        } else {
            return PrimitiveContext.create(new String((char[]) object));
        }
    }

    @Override
    public @NotNull Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException {
        if (reference == char[].class) {
            if (context.isObjectContext()) {
                return context.getAsObjectContext().getAsString().toCharArray();
            } else if (context.isArrayContext()) {
                @NotNull ArrayContext iterable = context.getAsArrayContext();
                @NotNull StringBuilder builder = new StringBuilder();

                while (true) {
                    try {
                        builder.append(iterable.readChar());
                    } catch (@NotNull EOFException ignore) {
                        return builder.toString().toCharArray();
                    }
                }
            } else {
                throw new UnsupportedOperationException("context not supported by char[] adapter '" + context + "'");
            }
        } else if (reference == Character[].class) {
            if (context.isObjectContext()) {
                return context.getAsObjectContext().getAsString().toCharArray();
            } else if (context.isArrayContext()) {
                @NotNull ArrayContext iterable = context.getAsArrayContext();
                @NotNull StringBuilder builder = new StringBuilder();

                while (true) {
                    try {
                        builder.append((Character) iterable.readChar());
                    } catch (@NotNull EOFException ignore) {
                        return builder.toString().toCharArray();
                    }
                }
            } else {
                throw new UnsupportedOperationException("context not supported by Character[] adapter '" + context + "'");
            }
        } else {
            throw new UnsupportedOperationException("the reference '" + reference + "' cannot be used at the character array adapter");
        }
    }

}
