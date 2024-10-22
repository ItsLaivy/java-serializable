package codes.laivy.serializable.utilities;

import codes.laivy.serializable.annotations.BypassTransient;
import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.annotations.Concretes;
import codes.laivy.serializable.annotations.KnownAs;
import codes.laivy.serializable.context.MapContext;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public final class Classes {

    // Static initializers

    public static @NotNull References getReferences(@NotNull Field field) {
        @NotNull List<Class<?>> classes = new LinkedList<>();

        if (isConcrete(field.getType())) {
            classes.add(field.getType());
        }

        if (field.isAnnotationPresent(Concretes.class)) {
            classes.addAll(Arrays.stream(field.getAnnotationsByType(Concretes.class)).flatMap(c -> Arrays.stream(c.value())).map(Concrete::type).collect(Collectors.toList()));
        } if (field.isAnnotationPresent(Concrete.class)) {
            classes.addAll(Arrays.stream(field.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toList()));
        }

        for (@NotNull Class<?> reference : classes) {
            if (!isConcrete(reference)) {
                throw new IllegalStateException("the reference '" + reference.getName() + "' isn't concrete!");
            }
        }

        return References.of(classes);
    }
    public static @NotNull Map<String, Field> getFields(@Nullable SerializationProperties.Father father, final @NotNull Class<?> reference) {
        @NotNull Map<String, Field> map = new LinkedHashMap<>();
        @NotNull Map<String, Integer> repeat = new HashMap<>();

        @NotNull Class<?> temp = reference;
        while (temp != Object.class && temp != null) {
            @NotNull Set<Field> fields = Arrays.stream(temp.getDeclaredFields()).collect(Collectors.toSet());

            for (@NotNull Field field : fields) {
                // @BypassTransient annotation
                boolean bypassTransients = father != null && father.getField().isAnnotationPresent(BypassTransient.class);

                // Check if is static or transient
                if (Modifier.isStatic(field.getModifiers()) || (!bypassTransients && Modifier.isTransient(field.getModifiers()))) {
                    continue;
                }

                @NotNull String name = field.getName();

                // Known as variable
                if (field.isAnnotationPresent(KnownAs.class)) {
                    @NotNull KnownAs known = field.getAnnotation(KnownAs.class);
                    name = known.name();

                    if (map.containsKey(name)) {
                        throw new IllegalStateException("there's two or more fields with the same @KnownAs name at the class '" + reference + "', check it's super classes.");
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
                        throw new IllegalStateException("cannot reserve a custom name for field '" + name + "' from class '" + reference + "'");
                    }

                    repeat.putIfAbsent(name, 0);
                    repeat.put(name, repeat.get(name) + 1);
                }
            }

            temp = temp.getSuperclass();
        }

        return map;
    }

    public static @Nullable Class<?> getConcrete(@NotNull Class<?> original, @NotNull Class<?> @NotNull [] classes, @NotNull MapContext context) {
        if (isCompatible(original, context)) {
            return original;
        } else for (@NotNull Class<?> concrete : classes) {
            if (isCompatible(concrete, context)) {
                return concrete;
            }
        }

        return null;
    }
    public static boolean isCompatible(@NotNull Class<?> reference, @NotNull MapContext context) {
        @NotNull Set<String> fields = getFields(null, reference).keySet();
        int mod = reference.getModifiers();

        if (Modifier.isInterface(mod) || Modifier.isAbstract(mod)) {
            return false;
        } else for (@NotNull String name : context.keySet()) {
            if (!fields.contains(name)) {
                return false;
            }
        }

        return true;
    }
    public static boolean isConcrete(@NotNull Class<?> reference) {
        if (reference.isArray()) {
            return isConcrete(reference.getComponentType());
        } else if (reference.isPrimitive()) {
            return true;
        }

        return !Modifier.isInterface(reference.getModifiers()) && !Modifier.isAbstract(reference.getModifiers());
    }

    // Object

    private Classes() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
