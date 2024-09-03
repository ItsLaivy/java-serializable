package codes.laivy.serializable.json;

import codes.laivy.serializable.annotations.KnownAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

final class FieldUtils {

    // Static initializers

    public static @NotNull Map<String, Field> getFields(final @NotNull Class<?> type) {
        @NotNull Map<String, Field> map = new LinkedHashMap<>();
        @NotNull Map<String, Integer> repeat = new HashMap<>();

        @NotNull Class<?> temp = type;
        while (temp != Object.class) {
            @NotNull Set<Field> fields = Arrays.stream(temp.getDeclaredFields()).collect(Collectors.toSet());

            for (@NotNull Field field : fields) {
                @NotNull String name = field.getName();

                // Known as variable
                if (field.isAnnotationPresent(KnownAs.class)) {
                    @NotNull KnownAs known = field.getAnnotation(KnownAs.class);
                    name = known.name();

                    if (map.containsKey(name)) {
                        throw new IllegalStateException("there's two or more fields with the same @KnownAs name at the class '" + type + "', check it's super classes.");
                    } else {
                        map.put(name, field);
                    }
                } else {
                    // Reserve name
                    if (!map.containsKey(name)) {
                        map.put(name, field);
                    } else if (!map.containsKey(name + "_" + repeat.get(name))) {
                        map.put(name + "_" + repeat.get(name), field);
                    } else if (!map.containsKey("$" + name + "_" + repeat.get(name))) {
                        map.put(name + "_" + repeat.get(name), field);
                    } else {
                        throw new IllegalStateException("cannot reserve a custom name for field '" + name + "' from class '" + type + "'");
                    }

                    repeat.putIfAbsent(name, 0);
                    repeat.put(name, repeat.get(name) + 1);
                }
            }

            temp = temp.getSuperclass();
        }

        return map;
    }
    public static @Nullable Field getFieldByName(@NotNull Object object, @NotNull String name) {
        return getFields(object.getClass()).get(name);
    }

    public static void checkClass(@NotNull Class<?> reference) throws InvalidClassException {
        // Check for @KnownAs annotations with the same name
        boolean hasDuplicates = getFields(reference).values().stream().map(field -> field.getAnnotation(KnownAs.class)).filter(Objects::nonNull).map(KnownAs::name).collect(Collectors.groupingBy(name -> name, Collectors.counting())).values().stream().anyMatch(count -> count > 1);

        if (hasDuplicates) {
            throw new InvalidClassException("there's two or more @KnownAs annotations with the same name!");
        }

        // Check for static fields using the @KnownAs annotation
        boolean hasStaticFieldsWithKnownAs = getFields(reference).values().stream().anyMatch(field -> Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(KnownAs.class));

        if (hasStaticFieldsWithKnownAs) {
            throw new InvalidClassException("the @KnownAs annotation cannot be used in static fields!");
        }

        // Check for transient fields using the @KnownAs annotation
        boolean hasTransientFieldsWithKnownAs = getFields(reference).values().stream().anyMatch(field -> Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(KnownAs.class));

        if (hasTransientFieldsWithKnownAs) {
            throw new InvalidClassException("the @KnownAs annotation cannot be used in transient fields!");
        }
    }

    // Object

    private FieldUtils() {
        throw new UnsupportedOperationException();
    }

}
