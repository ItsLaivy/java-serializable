package codes.laivy.serializable.utilities;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.BypassTransient;
import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.annotations.Concretes;
import codes.laivy.serializable.annotations.KnownAs;
import codes.laivy.serializable.context.ArrayContext;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.MapContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static codes.laivy.serializable.config.Config.Father;

@ApiStatus.Internal
public final class Classes {

    // Static initializers

    public static @NotNull Map<Type, Collection<Class<?>>> getGenericTypes(@NotNull AnnotatedType primary) {
        // Generics
        @NotNull Map<Type, Collection<Class<?>>> genericConcretes = new LinkedHashMap<>();

        @NotNull LinkedList<AnnotatedElement> elements = new LinkedList<>();
        elements.add(primary);

        int count = 0;
        while (!elements.isEmpty()) try {
            @NotNull AnnotatedElement element = elements.poll();

            if (element instanceof AnnotatedType) {
                @NotNull AnnotatedType annotated = (AnnotatedType) element;
                @NotNull Type type = annotated.getType();

                if (element instanceof AnnotatedParameterizedType) {
                    @NotNull AnnotatedParameterizedType parameterized = (AnnotatedParameterizedType) element;
                    elements.addAll(Arrays.asList(parameterized.getAnnotatedActualTypeArguments()));
                }

                // Skip the first annotated element values to not catch field concretes
                if (count == 0) {
                    continue;
                }

                genericConcretes.putIfAbsent(type, new LinkedHashSet<>());
                if (type instanceof Class && isConcrete((Class<?>) type)) {
                    genericConcretes.get(type).add((Class<?>) type);
                } if (annotated.isAnnotationPresent(Concrete.class)) {
                    genericConcretes.get(type).add(annotated.getAnnotation(Concrete.class).type());
                } if (annotated.isAnnotationPresent(Concretes.class)) {
                    genericConcretes.get(type).addAll(Arrays.stream(annotated.getAnnotationsByType(Concretes.class)).flatMap(concretes -> Arrays.stream(concretes.value())).map(Concrete::type).collect(Collectors.toList()));
                }
            }
        } finally {
            count++;
        }

        return genericConcretes;
    }

    public static @Nullable Object callWriteReplace(@NotNull Object object, boolean ignoreCasting) throws NoSuchMethodException {
        @NotNull Class<?> reference = object.getClass();
        @NotNull Method method = reference.getDeclaredMethod("writeReplace");
        method.setAccessible(true);

        try {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new NoSuchMethodException("the #" + method.getName() + " method cannot be static");
            } else if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                throw new NoSuchMethodException("invalid #" + method.getName() + " method return type");
            }

            object = method.invoke(object);

            if (!ignoreCasting && object != null && !reference.isAssignableFrom(reference)) {
                throw new ClassCastException("the #" + method.getName() + " method returns a type (" + reference + ") that isn't assignable with the object class '" + reference + "'");
            }

            return object;
        } catch (@NotNull InvocationTargetException | @NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot execute #" + method.getName() + " method", e);
        }
    }
    public static @Nullable Object callReadResolve(@NotNull Object object, boolean ignoreCasting) throws NoSuchMethodException {
        @NotNull Class<?> reference = object.getClass();
        @NotNull Method method = reference.getDeclaredMethod("readResolve");
        method.setAccessible(true);

        try {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new NoSuchMethodException("the #" + method.getName() + " method cannot be static");
            } else if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                throw new NoSuchMethodException("invalid #" + method.getName() + " method return type");
            }

            object = method.invoke(object);

            if (!ignoreCasting && object != null && !reference.isAssignableFrom(reference)) {
                throw new ClassCastException("the #" + method.getName() + " method returns a type (" + reference + ") that isn't assignable with the object class '" + reference + "'");
            }

            return object;
        } catch (@NotNull InvocationTargetException | @NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot execute #" + method.getName() + " method", e);
        }
    }

    public static @Nullable Field getOuterClassField(@NotNull Class<?> reference) {
        @Nullable Field field = null;
        int amount = 0;

        while (field == null) {
            @NotNull StringBuilder string = new StringBuilder("this$0");
            for (int row = 0; row < amount; row++) {
                string.append("$");
            }

            try {
                field = reference.getDeclaredField(string.toString());
            } catch (NoSuchFieldException e) {
                return null;
            }

            if (!field.isSynthetic()) field = null;

            amount++;
        }

        // Finish
        return field;
    }

    public static @NotNull Set<Class<?>> getReferences(@NotNull Class<?> reference) {
        @NotNull Set<Class<?>> references = new LinkedHashSet<>();
        references.add(reference);

        if (reference.isAnnotationPresent(Concretes.class)) {
            references.addAll(Arrays.stream(reference.getAnnotationsByType(Concretes.class)).flatMap(c -> Arrays.stream(c.value())).map(Concrete::type).collect(Collectors.toList()));
        } if (reference.isAnnotationPresent(Concrete.class)) {
            references.addAll(Arrays.stream(reference.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toList()));
        }

        for (@NotNull Class<?> temp : references) {
            if (reference != temp && !isConcrete(temp)) {
                throw new IllegalStateException("the reference '" + temp.getName() + "' isn't concrete!");
            }
        }

        return references;
    }
    public static @NotNull Set<Class<?>> getReferences(@NotNull Field field) {
        @NotNull Set<Class<?>> classes = new LinkedHashSet<>();

        if (field.isAnnotationPresent(Concretes.class) || field.isAnnotationPresent(Concrete.class)) {
            classes.add(field.getType());

            if (field.isAnnotationPresent(Concretes.class)) {
                classes.addAll(Arrays.stream(field.getAnnotationsByType(Concretes.class)).flatMap(c -> Arrays.stream(c.value())).map(Concrete::type).collect(Collectors.toList()));
            } if (field.isAnnotationPresent(Concrete.class)) {
                classes.addAll(Arrays.stream(field.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toList()));
            }
        } else {
            classes.addAll(Classes.getReferences(field.getType()));
        }

        for (@NotNull Class<?> reference : classes) {
            if (reference != field.getType() && !isConcrete(reference)) {
                throw new IllegalStateException("the reference '" + reference.getName() + "' isn't concrete!");
            }
        }

        return classes;
    }
    public static @NotNull Map<String, Field> getFields(@Nullable Father father, final @NotNull Class<?> reference) {
        @NotNull Map<String, Field> map = new LinkedHashMap<>();
        @NotNull Map<String, Integer> repeat = new HashMap<>();

        @NotNull Class<?> temp = reference;

        while (temp != Object.class && temp != null) {
            for (@NotNull Field field : temp.getDeclaredFields()) {
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

    public static boolean usesJavaSerialization(@NotNull Class<?> reference) {
        if (Externalizable.class.isAssignableFrom(reference)) {
            return true;
        }

        boolean methods = false;
        @NotNull Class<?> copy = reference;

        while (copy != Object.class && copy != null) {
            @NotNull Method method;

            try {
                method = copy.getDeclaredMethod("writeObject", ObjectOutputStream.class);
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            } try {
                method = copy.getDeclaredMethod("readObject", ObjectInputStream.class);
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            } try {
                method = copy.getDeclaredMethod("readObjectNoData");
                if (!Modifier.isStatic(method.getModifiers())) methods = true;
            } catch (@NotNull NoSuchMethodException ignore) {
            }

            // todo: #writeReplace and #readResolve methods

            copy = copy.getSuperclass();
        }

        if (methods && !Serializable.class.isAssignableFrom(reference)) {
            throw new IllegalStateException("the class '" + reference + "' has serialization methods but doesn't implement Serializable interface");
        }

        return methods;
    }
    public static <E> @Nullable E javaDeserializeObject(@NotNull Class<?> reference, @NotNull ArrayContext context) throws IOException, ClassNotFoundException {
        if (context.isNull()) {
            return null;
        }

        // Byte array
        byte[] bytes = new byte[context.size()];

        int row = 0;
        for (@NotNull Context element : context) {
            bytes[row] = element.getAsPrimitive().getAsByte();
            row++;
        }

        // Deserialize using object input stream
        @NotNull ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        //noinspection unchecked
        return (E) stream.readObject();
    }
    public static @NotNull ArrayContext javaSerializeObject(@NotNull Serializer serializer, @NotNull Object object) {
        try {
            @NotNull ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            @NotNull ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(object);

            // Byte array adapter
            @NotNull ArrayContext context = ArrayContext.create(serializer);

            for (byte b : bytes.toByteArray()) {
                context.write(b);
            }

            return context;
        } catch (@NotNull IOException e) {
            throw new RuntimeException("cannot serialize java object '" + object + "' from class '" + object.getClass().getName() + "'", e);
        }
    }

    // Object

    private Classes() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
