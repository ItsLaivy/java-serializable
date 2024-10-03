package codes.laivy.serializable.json;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.annotations.*;
import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import codes.laivy.serializable.exception.MalformedSerializerException;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

abstract class SerializingType {

    protected final @NotNull JsonSerializer json;
    protected final @Nullable Father father;

    private SerializingType(@NotNull JsonSerializer json, @Nullable Father father) {
        this.json = json;
        this.father = father;
    }

    // Serializers

    public abstract @NotNull JsonElement serialize(@Nullable Object object);
    public abstract <T> @Nullable T deserialize(@NotNull Class<T> reference, @NotNull JsonElement element);

    // Classes

    public static final class Normal extends SerializingType {

        // Static initializers

        static @NotNull Map<String, Field> getFields(@Nullable Father father, final @NotNull Class<?> type) {
            @NotNull Map<String, Field> map = new LinkedHashMap<>();
            @NotNull Map<String, Integer> repeat = new HashMap<>();

            @NotNull Class<?> temp = type;
            while (temp != Object.class) {
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

        // Object

        Normal(@NotNull JsonSerializer serializer, @Nullable Father father) {
            super(serializer, father);
        }

        @Override
        public @NotNull JsonElement serialize(@Nullable Object object) {
            if (object == null) {
                return JsonNull.INSTANCE;
            }

            @NotNull Class<?> type = object.getClass();
            @NotNull Map<String, Field> fields = getFields(father, type);

            // Fields
            if (father != null) {
                // @BypassTransient annotation
                boolean bypassTransients = father.getField().isAnnotationPresent(BypassTransient.class);

                if (!bypassTransients) {
                    fields.values().removeIf(field -> Modifier.isTransient(field.getModifiers()));
                }

                // @ExcludeFields and @OnlyFields annotation
                if (father.getField().isAnnotationPresent(OnlyFields.class)) {
                    @NotNull Set<String> only = father.getField().isAnnotationPresent(OnlyFields.class) ? new HashSet<>(Arrays.asList(father.getField().getAnnotation(OnlyFields.class).fields())) : new HashSet<>();
                    fields.values().removeIf(field -> !only.contains(field.getName()));
                } else if (father.getField().isAnnotationPresent(ExcludeFields.class)) {
                    @NotNull Set<String> excluded = father.getField().isAnnotationPresent(ExcludeFields.class) ? new HashSet<>(Arrays.asList(father.getField().getAnnotation(ExcludeFields.class).fields())) : new HashSet<>();
                    fields.values().removeIf(field -> excluded.contains(field.getName()));
                }
            }

            // Start serialization
            @NotNull JsonObject json = new JsonObject();

            // Start looking fields into class and superclasses
            for (@NotNull Entry<String, Field> entry : fields.entrySet()) {
                @NotNull String name = entry.getKey();
                @NotNull Field field = entry.getValue();

                try {
                    field.setAccessible(true);

                    @NotNull SerializingProcess serialization = new SerializingProcess(this.json, new Father(field, object));
                    json.add(name, serialization.serialize(field.get(object)));
                } catch (@NotNull IllegalAccessException e) {
                    throw new RuntimeException("cannot access field '" + field.getName() + "'", e);
                }
            }

            return json;
        }
        @Override
        public <T> @Nullable T deserialize(@NotNull Class<T> reference, @NotNull JsonElement element) {
            try {
                return (T) deserializeUnsafe(reference, element);
            } catch (InvalidClassException e) {
                throw new RuntimeException(e);
            }
        }

        private @Nullable Object deserializeUnsafe(@NotNull Class<?> reference, @NotNull JsonElement element) throws InvalidClassException {
            if (element.isJsonNull()) {
                return null;
            } else if (element.isJsonObject()) {
                @NotNull Map<String, Field> fields = getFields(father, reference);
                @NotNull JsonObject jsonObject = element.getAsJsonObject();
                @NotNull Object instance;

                if ((father != null && father.getField().isAnnotationPresent(UseEmptyConstructor.class)) || reference.isAnnotationPresent(UseEmptyConstructor.class)) try {
                    @NotNull Constructor<?> constructor = reference.getDeclaredConstructor();
                    constructor.setAccessible(true);

                    instance = constructor.newInstance();
                } catch (@NotNull NoSuchMethodException e) {
                    throw new RuntimeException("cannot find empty constructor at class '" + reference + "'");
                } catch (@NotNull InvocationTargetException | @NotNull InstantiationException | @NotNull IllegalAccessException e) {
                    throw new RuntimeException("cannot generate empty constructor instance at class '" + reference + "'", e);
                } else {
                    instance = Allocator.allocate(reference);
                }

                for (@NotNull String key : jsonObject.keySet()) {
                    @NotNull JsonElement value = jsonObject.get(key);
                    @Nullable Field field = fields.getOrDefault(key, null);

                    if (field == null) {
                        throw new InvalidClassException("there's no field with name '" + key + "'");
                    }

                    if (value.isJsonNull()) {
                        Allocator.setFieldValue(field, instance, null);
                    } else {
                        @Nullable Object object;

                        if (field.getType() == Class.class) try {
                            object = Class.forName(value.getAsString());
                        } catch (@NotNull ClassNotFoundException e) {
                            throw new InvalidClassException("there's no class '" + value.getAsString() + "' to deserialize at runtime");
                        } else {
                            @NotNull SerializingProcess process = new SerializingProcess(super.json, new Father(field, instance));
                            object = process.deserialize(value);
                        }

                        Allocator.setFieldValue(field, instance, object);
                    }
                }

                return instance;
            } else if (element.isJsonPrimitive()) {
                if (reference == String.class) {
                    return element.getAsString();
                } else if (reference == Boolean.class || reference == boolean.class) {
                    return element.getAsBoolean();
                } else if (reference == Character.class || reference == char.class) {
                    return element.getAsString().charAt(0);
                } else if (reference == Byte.class || reference == byte.class) {
                    return element.getAsByte();
                } else if (reference == Short.class || reference == short.class) {
                    return element.getAsShort();
                } else if (reference == Integer.class || reference == int.class) {
                    return element.getAsInt();
                } else if (reference == Long.class || reference == long.class) {
                    return element.getAsLong();
                } else if (reference == Float.class || reference == float.class) {
                    return element.getAsFloat();
                } else if (reference == Double.class || reference == double.class) {
                    return element.getAsDouble();
                } else {
                    throw new UnsupportedOperationException("there's no primitive type with reference '" + reference + "', is missing any adapter here?");
                }
            } else {
                throw new UnsupportedOperationException("cannot deserialize '" + element + "' into a valid '" + reference + "' object");
            }
        }

    }
    public static final class Methods extends SerializingType {

        // Static initializers

        private static @NotNull Method getSerializerMethod(@NotNull Class<?> reference, @NotNull UsingSerializers annotation) {
            @NotNull String string = annotation.serialization();
            @NotNull String[] parts = string.split("#");
            @NotNull String name;

            if (parts.length == 0 || parts.length > 2) {
                throw new IllegalArgumentException("illegal serializer method reference '" + string + "'");
            } else if (parts.length == 2) try {
                if (!parts[0].isEmpty()) {
                    reference = Class.forName(parts[0]);
                }

                name = parts[1];
            } catch (@NotNull ClassNotFoundException e) {
                throw new RuntimeException("cannot find class '" + parts[0] + "' from @UsingSerializers annotation", e);
            } else {
                name = parts[0];
            }

            for (@NotNull Method method : reference.getDeclaredMethods()) {
                if (method.getName().equals(name) && Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 2 && method.getParameters()[1].getType() == SerializeOutputContext.class) {
                    return method;
                }
            }

            throw new MalformedSerializerException("there's no valid serialize method named '" + name + "' at class '" + reference + "'");
        }
        private static @NotNull Method getDeserializerMethod(@NotNull Class<?> reference, @NotNull UsingSerializers annotation) {
            @NotNull String string = annotation.deserialization();
            @NotNull String[] parts = string.split("#");
            @NotNull String name;

            if (parts.length == 0 || parts.length > 2) {
                throw new IllegalArgumentException("illegal serializer method reference '" + string + "'");
            } else if (parts.length == 2) try {
                if (!parts[0].isEmpty()) {
                    reference = Class.forName(parts[0]);
                }

                name = parts[1];
            } catch (@NotNull ClassNotFoundException e) {
                throw new RuntimeException("cannot find class '" + parts[0] + "' from @UsingSerializers annotation", e);
            } else {
                name = parts[0];
            }

            for (@NotNull Method method : reference.getDeclaredMethods()) {
                if (method.getName().equals(name) && Modifier.isStatic(method.getModifiers()) && method.getReturnType() != void.class && method.getParameterCount() == 1 && method.getParameters()[0].getType() == SerializeInputContext.class) {
                    return method;
                }
            }

            throw new MalformedSerializerException("there's no valid deserialize method named '" + name + "' at class '" + reference + "'");
        }

        // Object

        private final @NotNull Method serializer;
        private final @NotNull Method deserializer;

        Methods(@NotNull JsonSerializer serializer, @Nullable Father father, @NotNull Class<?> reference, @NotNull UsingSerializers annotation) {
            super(serializer, father);

            this.serializer = getSerializerMethod(reference, annotation);
            this.deserializer = getDeserializerMethod(reference, annotation);
        }

        @Override
        public @NotNull JsonElement serialize(@Nullable Object object) {
            try {
                if (object == null) {
                    return JsonNull.INSTANCE;
                }

                serializer.setAccessible(true);

                if (!serializer.getParameters()[0].getType().isAssignableFrom(object.getClass())) {
                    throw new UnsupportedOperationException("the serializer cannot be used by reference '" + object.getClass() + "' because it's not a subclass/implementation from '" + serializer.getParameters()[0].getType() + "' parameter class");
                }

                // Write into output context
                @NotNull JsonSerializeOutputContext context = new JsonSerializeOutputContext(super.json, object.getClass());
                serializer.invoke(null, object, context);

                // Convert it into a json
                @NotNull JsonElement json = context.serialize();

                return json;
            } catch (@NotNull IllegalAccessException | @NotNull InvocationTargetException e) {
                throw new RuntimeException("cannot execute serialize method from @UsingSerializers annotation", e);
            }
        }
        @Override
        public <T> @Nullable T deserialize(@NotNull Class<T> reference, @NotNull JsonElement element) {
            try {
                deserializer.setAccessible(true);

                if (!deserializer.getReturnType().isAssignableFrom(reference)) {
                    throw new UnsupportedOperationException("the deserializer cannot be used by reference '" + reference + "' because it's not a subclass/implementation from '" + deserializer.getReturnType() + "' return class");
                }

                // Start deserialize
                @NotNull JsonSerializeInputContext context = new JsonSerializeInputContext(super.json, reference, element);
                @Nullable Object object = deserializer.invoke(null, context);

                // Finish
                //noinspection unchecked
                return (T) object;
            } catch (@NotNull IllegalAccessException | @NotNull InvocationTargetException e) {
                throw new RuntimeException("cannot execute deserialize method from @UsingSerializers annotation", e);
            }
        }

    }

}
