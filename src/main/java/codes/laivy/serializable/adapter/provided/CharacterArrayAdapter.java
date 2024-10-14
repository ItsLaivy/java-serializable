package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.EOFException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    public void serialize(@NotNull Object object, @NotNull SerializeOutputContext context) {
        if (object instanceof Character[]) {
            @Nullable Character[] instance = (Character[]) object;
            boolean anyNull = Arrays.stream(instance).anyMatch(Objects::isNull);

            if (anyNull) { // There's an impostor here...
                for (@Nullable Character c : instance) {
                    if (c == null) context.write(null);
                    else context.write(c);
                }
            } else { // All clean to be a plain text!
                @NotNull StringBuilder builder = new StringBuilder();
                for (@UnknownNullability Character c : instance) {
                    builder.append(c.charValue());
                }

                context.write(builder.toString());
            }
        } else {
            context.write(new String((char[]) object));
        }
    }
    @Override
    public @NotNull Object deserialize(@NotNull SerializeInputContext context) throws EOFException {
        if (context.getReference() == char[].class) {
            @NotNull StringBuilder builder = new StringBuilder();

            while (true) {
                try {
                    builder.append(context.readLine());
                } catch (@NotNull EOFException ignore) {
                    return builder.toString().toCharArray();
                }
            }
        } else if (context.getReference() == Character[].class) {
            @NotNull List<Character> list = new LinkedList<>();

            while (true) {
                try {
                    @Nullable String string = context.readLine();

                    if (string == null) {
                        list.add(null);
                    } else for (char c : string.toCharArray()) {
                        list.add(c);
                    }
                } catch (@NotNull EOFException ignore) {
                    return list.toArray(new Character[0]);
                }
            }
        } else {
            throw new UnsupportedOperationException("the reference '" + context.getReference() + "' cannot be used at the character array adapter");
        }
    }

}
