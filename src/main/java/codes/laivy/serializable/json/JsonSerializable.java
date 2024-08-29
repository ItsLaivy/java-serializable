package codes.laivy.serializable.json;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.annotations.KnownAs;
import com.google.gson.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class designed to facilitate the serialization and deserialization of Java objects to and from JSON format.
 * <p>
 * The {@code JsonSerializable} class offers a variety of configuration options that allow developers to control the serialization
 * process, including the handling of custom field names via the {@code @KnownAs} annotation, the inclusion of serial version
 * information, and the option to disable safety checks for performance improvements.
 * </p>
 * <p>
 * This class supports the serialization of both primitive data types and complex objects, including those that implement
 * the {@link Serializable} interface. It also provides deserialization capabilities, allowing JSON data to be converted back
 * into corresponding Java objects.
 * </p>
 * Key features of this class include:
 * <ul>
 *     <li>Support for custom field name mappings using the {@code @KnownAs} annotation.</li>
 *     <li>Optional inclusion of the {@code serialVersionUID} property in serialized JSON output for version control.</li>
 *     <li>Configurable safety checks to enhance performance in critical scenarios.</li>
 *     <li>Ability to handle a wide range of data types, including complex nested structures.</li>
 * </ul>
 * Example usage:
 * <pre>{@code
 * JsonSerializable serializer = new JsonSerializable();
 * String json = serializer.serialize(myObject).toString();
 * }</pre>
 * <p>
 * This class is designed to be used as a utility, with its methods returning the same instance to allow for fluent method chaining.
 * It is recommended to carefully configure the options before performing serialization or deserialization to ensure the
 * expected behavior and output.
 * </p>
 *
 * <p><strong>Note:</strong> When using the {@code unsafeMode}, developers should be aware of the potential risks and ensure that
 * the objects being serialized are well-formed and free from errors. Disabling safety checks can lead to significant performance
 * gains but may also introduce vulnerabilities if not handled properly.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @version 1.0
 * @see Serializable
 * @see JsonElement
 * @see JsonObject
 * @see KnownAs
 */
public final class JsonSerializable {

    // Object

    private boolean explicitSerial = false;
    private boolean unsafeMode = false;
    private boolean ignoreKnownAs = false;

    public JsonSerializable() {
    }

    // Builder

    /**
     * Sets whether the serialized JSON should include the "serialVersionUID" property.
     * <p>
     * When this option is enabled (set to {@code true}), the serialized JSON will include a "serialVersionUID"
     * property that corresponds to the `serialVersionUID` of the serialized class. This is useful for ensuring
     * compatibility between different versions of a serialized object during deserialization.
     * Additionally, during deserialization, the `serialVersionUID` will be checked to verify that the class matches
     * the serialized version.
     * </p>
     *
     * @param explicitSerial a boolean value indicating whether to include "serialVersionUID" in the serialized JSON.
     *                       When {@code true}, the "serialVersionUID" will be included in the output JSON. When {@code false},
     *                       it will be excluded.
     * @return the current instance of {@link JsonSerializable}, allowing for method chaining.
     */
    @Contract(value = "_->this")
    public @NotNull JsonSerializable setExplicitSerial(boolean explicitSerial) {
        this.explicitSerial = explicitSerial;
        return this;
    }

    /**
     * Returns whether the "serialVersionUID" property will be included in the serialized JSON.
     * <p>
     * This method checks the current state of the `explicitSerial` flag, which controls whether the "serialVersionUID"
     * is included in the serialized JSON. If {@code true}, the JSON output will contain the "serialVersionUID" property.
     * If {@code false}, it will be omitted.
     * </p>
     *
     * @return a boolean indicating whether the "serialVersionUID" property is enabled for serialization.
     */
    public boolean isExplicitSerial() {
        return explicitSerial;
    }

    /**
     * Configures whether to ignore the `@KnownAs` annotation during serialization.
     * <p>
     * When this option is enabled (set to {@code true}), fields annotated with `@KnownAs` will be serialized using
     * their original field names instead of the names specified in the `@KnownAs` annotation. However, even with this
     * option enabled, the serializer will still check for duplicate `@KnownAs` names and other potential issues.
     * The deserializer will still recognize both the original and annotated names, ensuring compatibility.
     * </p>
     *
     * @param ignoreKnownAs a boolean value indicating whether to ignore the `@KnownAs` annotation during serialization.
     *                      When {@code true}, the original field names will be used. When {@code false}, the names specified
     *                      in the `@KnownAs` annotation will be used.
     * @return the current instance of {@link JsonSerializable}, allowing for method chaining.
     */
    @Contract(value = "_->this")
    public @NotNull JsonSerializable setIgnoreKnownAs(boolean ignoreKnownAs) {
        this.ignoreKnownAs = ignoreKnownAs;
        return this;
    }

    /**
     * Returns whether the `@KnownAs` annotation will be ignored during serialization.
     * <p>
     * This method checks the current state of the `ignoreKnownAs` flag, which determines whether the `@KnownAs` annotation
     * is considered during serialization. If {@code true}, the original field names will be used in the serialized JSON.
     * If {@code false}, the names specified in the `@KnownAs` annotation will be used.
     * </p>
     *
     * @return a boolean indicating whether the `@KnownAs` annotation is ignored during serialization.
     */
    public boolean isIgnoreKnownAs() {
        return ignoreKnownAs;
    }

    /**
     * Configures whether to enable or disable safety checks during serialization.
     * <p>
     * When this option is enabled (set to {@code false}), the serializer will perform various safety checks,
     * such as validating annotations and ensuring the integrity of the serialized output. Disabling these checks
     * (setting this option to {@code true}) can improve serialization performance but may lead to issues if not used carefully.
     * It is recommended to use this option only when performance is critical and the serialized objects are well-formed.
     * </p>
     *
     * @param unsafeMode a boolean value indicating whether to disable safety checks during serialization.
     *                   When {@code true}, safety checks are disabled, potentially improving performance.
     *                   When {@code false}, safety checks are enabled, ensuring a more secure serialization process.
     * @return the current instance of {@link JsonSerializable}, allowing for method chaining.
     */
    @Contract(value = "_->this")
    public @NotNull JsonSerializable setUnsafeMode(boolean unsafeMode) {
        this.unsafeMode = unsafeMode;
        return this;
    }

    /**
     * Returns whether safety checks are disabled during serialization.
     * <p>
     * This method checks the current state of the `unsafeMode` flag, which determines whether the serializer
     * performs safety checks. If {@code true}, safety checks are disabled, which may increase performance but
     * could also introduce risks if the serialized objects are not carefully managed. If {@code false}, safety
     * checks are enabled, providing a more robust and secure serialization process.
     * </p>
     *
     * @return a boolean indicating whether safety checks are disabled during serialization.
     */
    public boolean isUnsafeMode() {
        return unsafeMode;
    }

    // Serializers

    /**
     * Serializes the given object into a {@link JsonElement}. This method is deprecated because
     * serializing objects that do not implement the {@link Serializable} interface directly can
     * lead to numerous problems and errors. It is recommended to use the generic version of this
     * method that ensures the object implements {@link Serializable}.
     *
     * @param object the object to be serialized. May be {@code null}.
     * @return the serialized {@link JsonElement}.
     * @throws InvalidClassException if the object's class is invalid for serialization.
     *
     * @deprecated Serializing objects that doesn't implement directly the {@link Serializable} interface may
     * cause A LOT OF ERRORS, don't ask for support if you use that method!
     */
    @Deprecated
    public @NotNull JsonElement serialize(@Nullable Object object) throws InvalidClassException {
        /*
         * Recursive serialization prevent map
         * Key = Class, Value = hashCode set
         */
        @NotNull Map<Class<?>, Set<Integer>> map = new HashMap<>();
        return serialize0(object, map);
    }

    /**
     * Serializes the given array of objects into a {@link JsonArray}. This method is deprecated
     * because serializing objects that do not implement the {@link Serializable} interface directly
     * can lead to numerous problems and errors. It is recommended to use the generic version of this
     * method that ensures the objects implement {@link Serializable}.
     *
     * @param array the array of objects to be serialized. Must not be {@code null}.
     * @return the serialized {@link JsonArray}.
     * @throws InvalidClassException if any object in the array's class is invalid for serialization.
     *
     * @deprecated Serializing objects that doesn't implement directly the {@link Serializable} interface may
     * cause A LOT OF ERRORS, don't ask for support if you use that method!
     */
    @Deprecated
    public @NotNull JsonArray serialize(@NotNull Object[] array) throws InvalidClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@NotNull Object object : array) {
            json.add(serialize(object));
        }

        return json;
    }

    /**
     * Serializes the given object that implements {@link Serializable} into a {@link JsonElement}.
     * This method ensures that the object implements {@link Serializable} and includes its
     * {@code serialVersionUID} if explicitly required.
     *
     * @param <T> the type of the object to be serialized.
     * @param object the object to be serialized. May be {@code null}.
     * @return the serialized {@link JsonElement}. If the object is {@code null}, returns
     *         {@link JsonNull#INSTANCE}.
     * @throws InvalidClassException if the object's class is invalid for serialization.
     */
    public <T extends Serializable> @NotNull JsonElement serialize(@Nullable T object) throws InvalidClassException {
        // Check if is null
        if (object == null) return JsonNull.INSTANCE;

        /*
         * Recursive serialization prevent map
         * Key = Class, Value = hashCode set
         */
        @NotNull Map<Class<?>, Set<Integer>> map = new HashMap<>();
        @NotNull JsonElement json = serialize0(object, map);

        // Get some essential information before serialization
        @NotNull ObjectStreamClass stream = ObjectStreamClass.lookup(object.getClass());
        long serial = stream.getSerialVersionUID();

        // Add serial
        if (json.isJsonObject() && isExplicitSerial()) {
            json.getAsJsonObject().addProperty("serialVersionUID", serial);
        }

        // Finish
        return json;
    }

    /**
     * Serializes the given array of {@link Serializable} objects into a {@link JsonArray}.
     *
     * @param <T> the type of the objects in the array. Must be {@link Serializable}.
     * @param array the array of {@link Serializable} objects to be serialized. Must not be {@code null}.
     * @return the serialized {@link JsonArray}.
     * @throws InvalidClassException if any object in the array's class is invalid for serialization.
     */
    public <T extends Serializable> @NotNull JsonArray serialize(T @NotNull [] array) throws InvalidClassException {
        @NotNull JsonArray json = new JsonArray();

        for (@NotNull Serializable serializable : array) {
            json.add(serialize(serializable));
        }

        return json;
    }

    private @NotNull JsonElement serialize0(@Nullable Object object, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
        // Check if is a primitive or known type
        if (object == null) return JsonNull.INSTANCE;

        if (object.getClass().isArray()) {
            @Nullable Object @NotNull [] array = toObjectArray(object);
            @NotNull JsonArray json = new JsonArray();

            for (@Nullable Object index : array) {
                json.add(serialize0(index, map));
            }

            return json;
        } else {
            if (object instanceof Boolean) return new JsonPrimitive((Boolean) object);
            else if (object instanceof Character) return new JsonPrimitive((Character) object);
            else if (object instanceof String) return new JsonPrimitive((String) object);
            else if (object instanceof Double) return new JsonPrimitive((Number) object);
            else if (object instanceof AtomicInteger) return new JsonPrimitive((Number) object);
            else if (object instanceof AtomicLong) return new JsonPrimitive((Number) object);
            else if (object instanceof BigDecimal) return new JsonPrimitive((Number) object);
            else if (object instanceof BigInteger) return new JsonPrimitive((Number) object);
            else if (object instanceof Byte) return new JsonPrimitive((Number) object);
            else if (object instanceof DoubleAccumulator) return new JsonPrimitive((Number) object);
            else if (object instanceof DoubleAdder) return new JsonPrimitive((Number) object);
            else if (object instanceof Float) return new JsonPrimitive((Number) object);
            else if (object instanceof Integer) return new JsonPrimitive((Number) object);
            else if (object instanceof Long) return new JsonPrimitive((Number) object);
            else if (object instanceof LongAccumulator) return new JsonPrimitive((Number) object);
            else if (object instanceof LongAdder) return new JsonPrimitive((Number) object);
            else if (object instanceof Short) return new JsonPrimitive((Number) object);

            // Check class for failures
            if (!isUnsafeMode()) checkClass(object);

            // Start serialization
            @NotNull JsonObject json = new JsonObject();
            @NotNull JsonArray writeObjectArray = new JsonArray();
            boolean hasWriteObjectData = false;

            // Start serializes the fields
            @NotNull Class<?> type = object.getClass();

            // Start looking fields into class and superclasses
            while (type != Object.class) {
                for (@NotNull Field field : getFields(type)) {
                    serialize0(json, object, field, map);
                }

                // Check for #writeObject availability and invoke it
                @Nullable Method writeObject = getWriteObjectField(type);
                @NotNull ByteArrayOutputStream stream = new ByteArrayOutputStream();

                if (writeObject != null) try (@NotNull ObjectOutputStream oss = new ObjectOutputStream(stream)) {
                    writeObject.setAccessible(true);
                    writeObject.invoke(object, oss);
                    writeObject.setAccessible(false);
                } catch (@NotNull IOException e) {
                    throw new RuntimeException("cannot create object output stream to perform #writeObject invoke from class '" + type + "'", e);
                } catch (@NotNull InvocationTargetException e) {
                    throw new RuntimeException("cannot execute #writeObject from class '" + type + "'", e);
                } catch (@NotNull IllegalAccessException e) {
                    throw new RuntimeException("cannot access #writeObject method from class '" + type + "'", e);
                }

                @NotNull JsonArray array = new JsonArray();
                for (byte b : stream.toByteArray()) {
                    array.add(b);
                }

                writeObjectArray.add(array);

                if (!array.isEmpty()) {
                    hasWriteObjectData = true;
                }

                // Finish with the superclass
                type = type.getSuperclass();
            }

            if (hasWriteObjectData) {
                json.add("#writeObject", writeObjectArray);
            }

            return json;
        }

    }
    private void serialize0(@NotNull JsonObject object, @NotNull Object instance, @NotNull Field field, @NotNull Map<Class<?>, Set<Integer>> map) throws InvalidClassException {
        try {
            // Check if not transient or static
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }

            // Check accessibility
            boolean accessible = field.isAccessible();
            if (!accessible) field.setAccessible(true);

            // Get value and prevent recursive serialization
            @Nullable Object value = field.get(instance);
            if (value != null) {
                int hash = value.hashCode();

                @NotNull Set<Integer> set = map.computeIfAbsent(value.getClass(), k -> new HashSet<>());
                if (set.contains(hash)) return;

                set.add(hash);
            }

            // Get field name (or @KnownAs name)
            @NotNull String name = field.getName();

            if (!isIgnoreKnownAs()) {
                if (field.isAnnotationPresent(KnownAs.class)) {
                    name = field.getAnnotation(KnownAs.class).name();
                }
            }

            // Serialize field value and add it to JSON
            @NotNull JsonElement serialized = serialize0(value, map);
            object.add(name, serialized);

            // Make it inaccessible again
            field.setAccessible(accessible);
        } catch (@NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot access field '" + field.getName() + "' to proceed serialization", e);
        }
    }

    // Deserializers

    /**
     * Deserializes the given {@link JsonElement} into an object of the specified type. This method is
     * deprecated and should not be used as it does not ensure that the object implements
     * {@link Serializable}, which could lead to various issues. Use the generic version of this method
     * to handle deserialization safely.
     *
     * @param type the class of the object to be deserialized. Must not be {@code null}.
     * @param element the {@link JsonElement} to be deserialized. Must not be {@code null}.
     * @return the deserialized object.
     * @throws InvalidClassException if the {@link JsonElement} cannot be deserialized into the specified type.
     * @throws InstantiationException if the class of the object cannot be instantiated.
     *
     * @deprecated Serializing objects that doesn't implement directly the {@link Serializable} interface may
     * cause A LOT OF ERRORS, don't ask for support if you use that method!
     */
    @Deprecated
    public @UnknownNullability Object deserialize(@NotNull Class<?> type, @NotNull JsonElement element) throws InvalidClassException, InstantiationException {
        return deserialize0(type, element);
    }

    /**
     * Deserializes the given {@link JsonObject} into an object of the specified type.
     *
     * @param <T> the type of the object to be deserialized.
     * @param type the class of the object to be deserialized. Must not be {@code null}.
     * @param json the {@link JsonObject} to be deserialized. Must not be {@code null}.
     * @return the deserialized object.
     * @throws InvalidClassException if the {@link JsonObject} cannot be deserialized into the specified type.
     * @throws InstantiationException if the class of the object cannot be instantiated.
     */
    public <T> @UnknownNullability T deserialize(@NotNull Class<T> type, @NotNull JsonObject json) throws InvalidClassException, InstantiationException {
        //noinspection unchecked
        return (T) deserialize0(type, json);
    }

    /**
     * Deserializes the given {@link JsonArray} into an array of objects of the specified type.
     *
     * @param <T> the type of the objects in the array.
     * @param type the class of the objects to be deserialized. Must not be {@code null}.
     * @param array the {@link JsonArray} to be deserialized. Must not be {@code null}.
     * @return the deserialized array of objects.
     * @throws InvalidClassException if the {@link JsonArray} cannot be deserialized into the specified type.
     * @throws InstantiationException if any object in the array's class cannot be instantiated.
     */
    public <T> @UnknownNullability T[] deserialize(@NotNull Class<T> type, @NotNull JsonArray array) throws InvalidClassException, InstantiationException {
        //noinspection unchecked
        return this.deserialize(type, array, (T[]) Array.newInstance(type, array.size()));
    }

    /**
     * Deserializes the given {@link JsonArray} into an array of objects of the specified type using
     * the provided array supplier function.
     *
     * @param <T> the type of the objects in the array.
     * @param type the class of the objects to be deserialized. Must not be {@code null}.
     * @param array the {@link JsonArray} to be deserialized. Must not be {@code null}.
     * @param intFunction a function that supplies an array of the appropriate size. Must not be {@code null}.
     * @return the deserialized array of objects.
     * @throws InvalidClassException if the {@link JsonArray} cannot be deserialized into the specified type.
     * @throws InstantiationException if any object in the array's class cannot be instantiated.
     */
    public <T> @UnknownNullability T[] deserialize(@NotNull Class<T> type, @NotNull JsonArray array, @NotNull IntFunction<T[]> intFunction) throws InvalidClassException, InstantiationException {
        return this.deserialize(type, array, intFunction.apply(array.size()));
    }

    /**
     * Deserializes the given {@link JsonArray} into an array of objects of the specified type,
     * storing the result in the provided array.
     *
     * @param <T> the type of the objects in the array.
     * @param type the class of the objects to be deserialized. Must not be {@code null}.
     * @param array the {@link JsonArray} to be deserialized. Must not be {@code null}.
     * @param store the array to store the deserialized objects. Must not be {@code null}.
     * @return the deserialized array of objects.
     * @throws InvalidClassException if the {@link JsonArray} cannot be deserialized into the specified type.
     * @throws InstantiationException if any object in the array's class cannot be instantiated.
     */
    public <T> @UnknownNullability T[] deserialize(@NotNull Class<T> type, @NotNull JsonArray array, @NotNull T[] store) throws InvalidClassException, InstantiationException {
        @NotNull List<T> list = new LinkedList<>();

        for (@NotNull JsonElement element : array) {
            list.add(deserialize(type, element.getAsJsonObject()));
        }

        return list.toArray(store);
    }

    private @UnknownNullability Object deserialize0(@NotNull Class<?> type, @NotNull JsonElement element) throws InvalidClassException, InstantiationException {
        if (element.isJsonNull()) {
            return null;
        } else if (type == String.class) {
            return element.getAsString();
        } else if (type == Boolean.class || type == boolean.class) {
            return element.getAsBoolean();
        } else if (type == Character.class || type == char.class) {
            return element.getAsString().charAt(0);
        } else if ((Number.class.isAssignableFrom(type) || type.isPrimitive()) && element.isJsonPrimitive()) {
            return element.getAsNumber();
        }

        if (type.isArray()) {
            if (element.isJsonArray()) {
                return deserialize(type, element.getAsJsonArray());
            } else {
                throw new InvalidClassException("array class type but element isn't an array json '" + element + "'");
            }
        } else if (element.isJsonObject()) try {
            @NotNull JsonObject object = element.getAsJsonObject();
            @NotNull Object instance = Allocator.allocate(type);

            for (@NotNull String key : object.keySet()) {
                @NotNull JsonElement value = object.get(key);

                if (key.equals("#writeObject")) {
                    if (!value.isJsonArray()) {
                        throw new InstantiationException("writeObject json data must be an array!");
                    }

                    @NotNull JsonArray array = value.getAsJsonArray();
                    @NotNull Class<?> clazz = instance.getClass();

                    for (@NotNull JsonArray inner : array.asList().stream().map(JsonElement::getAsJsonArray).toArray(JsonArray[]::new)) {
                        @Nullable Method method = getReadObjectField(clazz);

                        if (method != null) try {
                            byte[] bytes = new byte[inner.size()];
                            int row = 0;

                            for (@NotNull Byte b : inner.asList().stream().map(JsonElement::getAsByte).toArray(Byte[]::new)) {
                                bytes[row] = b;
                                row++;
                            }

                            @NotNull ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                            @NotNull ObjectInputStream ois = new ObjectInputStream(stream);

                            method.setAccessible(true);
                            method.invoke(instance, ois);
                            method.setAccessible(false);
                        } catch (@NotNull IOException e) {
                            throw new RuntimeException("cannot create object input stream to perform #readObject invoke from class '" + type + "'", e);
                        } catch (@NotNull InvocationTargetException e) {
                            throw new RuntimeException("cannot execute #readObject from class '" + type + "'", e);
                        } catch (@NotNull IllegalAccessException e) {
                            throw new RuntimeException("cannot access #readObject method from class '" + type + "'", e);
                        }

                        clazz = clazz.getSuperclass();
                    }
                } else {
                    @Nullable Field field = getFieldByName(instance, key);

                    if (field == null) {
                        throw new InvalidClassException("there's no field with name '" + key + "'");
                    }

                    field.setAccessible(true);

                    boolean isFinal = Modifier.isFinal(field.getModifiers());
                    if (isFinal) setFieldFinal(field, false);

                    try {
                        if (value.isJsonNull()) {
                            field.set(instance, null);
                        } else if (field.getType().isArray()) {
                            field.set(instance, deserialize(field.getType().getComponentType(), value.getAsJsonArray()));
                        } else {
                            field.set(instance, deserialize0(field.getType(), value));
                        }
                    } finally {
                        if (isFinal) setFieldFinal(field, true);
                    }
                }
            }

            return instance;
        } catch (@NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot deserialize json '" + element + "' into a valid '" + type.getName() + "' object due to an unexpected error", e);
        } else if (element.isJsonArray()) {
            throw new IllegalArgumentException("the specified json is an array, use #deserialize(Class<T>, JsonArray) instead!");
        } else {
            throw new UnsupportedOperationException("unknown json element type '" + element + "'");
        }
    }

    // Utilities

    private static void checkClass(@NotNull Object object) throws InvalidClassException {
        // Check for @KnownAs annotations with the same name
        boolean hasDuplicates = Arrays.stream(getFields(object.getClass())).map(field -> field.getAnnotation(KnownAs.class)).filter(Objects::nonNull).map(KnownAs::name).collect(Collectors.groupingBy(name -> name, Collectors.counting())).values().stream().anyMatch(count -> count > 1);

        if (hasDuplicates) {
            throw new InvalidClassException("there's two or more @KnownAs annotations with the same name!");
        }

        // Check for static fields using the @KnownAs annotation
        boolean hasStaticFieldsWithKnownAs = Arrays.stream(getFields(object.getClass())).anyMatch(field -> Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(KnownAs.class));

        if (hasStaticFieldsWithKnownAs) {
            throw new InvalidClassException("the @KnownAs annotation cannot be used in static fields!");
        }

        // Check for transient fields using the @KnownAs annotation
        boolean hasTransientFieldsWithKnownAs = Arrays.stream(getFields(object.getClass())).anyMatch(field -> Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(KnownAs.class));

        if (hasTransientFieldsWithKnownAs) {
            throw new InvalidClassException("the @KnownAs annotation cannot be used in transient fields!");
        }
    }

    private static @Nullable Field getFieldByName(@NotNull Object object, @NotNull String name) {
        for (@NotNull Field field : getFields(object.getClass())) {
            // Skip transient and static fields
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // Check and get by @KnownAs, will use the name otherwise.
            if (field.isAnnotationPresent(KnownAs.class)) {
                @NotNull KnownAs annotation = field.getAnnotation(KnownAs.class);

                if (annotation.name().equals(name)) {
                    return field;
                }
            } else if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }
    private static @NotNull Field[] getFields(@NotNull Class<?> type) {
        return Stream.of(type.getFields(), type.getDeclaredFields()).flatMap(Arrays::stream).distinct().toArray(Field[]::new);
    }

    private static @Nullable Object @NotNull [] toObjectArray(@NotNull Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("the provided object is not an array");
        }

        int length = Array.getLength(array);
        @Nullable Object @NotNull [] objectArray = new Object[length];

        for (int i = 0; i < length; i++) {
            objectArray[i] = Array.get(array, i);
        }

        return objectArray;
    }

    private static void setFieldFinal(@NotNull Field field, boolean isFinal) {
        try {
            @NotNull Field modifiersField = Field.class.getDeclaredField("modifiers");
            boolean accessible = modifiersField.isAccessible();
            if (!accessible) modifiersField.setAccessible(true);

            try {
                if (!isFinal) {
                    if (Modifier.isFinal(field.getModifiers())) {
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    }
                } else {
                    if (!Modifier.isFinal(field.getModifiers())) {
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    }
                }
            } finally {
                if (!accessible) modifiersField.setAccessible(false);
            }
        } catch (@NotNull NoSuchFieldException | @NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot mark field as" + (isFinal ? " " : " not") + " final", e);
        }
    }

    private static @Nullable Method getWriteObjectField(@NotNull Class<?> type) throws InvalidClassException {
        try {
            @NotNull Method method = type.getDeclaredMethod("writeObject", ObjectOutputStream.class);

            if (method.getReturnType() != void.class) {
                throw new InvalidClassException("the #writeObject method must return void");
            } else if (!Modifier.isPrivate(method.getModifiers())) {
                throw new InvalidClassException("the #writeObject method must be private");
            }

            return method;
        } catch (@NotNull NoSuchMethodException e) {
            return null;
        }
    }
    private static @Nullable Method getReadObjectField(@NotNull Class<?> type) throws InvalidClassException {
        try {
            @NotNull Method method = type.getDeclaredMethod("readObject", ObjectInputStream.class);

            if (method.getReturnType() != void.class) {
                throw new InvalidClassException("the #writeObject method must return void");
            } else if (!Modifier.isPrivate(method.getModifiers())) {
                throw new InvalidClassException("the #writeObject method must be private");
            }

            return method;
        } catch (@NotNull NoSuchMethodException e) {
            return null;
        }
    }

}