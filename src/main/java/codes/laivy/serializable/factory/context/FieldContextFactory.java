package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.context.ArrayContext;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.MapContext;
import codes.laivy.serializable.exception.MalformedClassException;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;

import static codes.laivy.serializable.properties.SerializationProperties.Father;
import static codes.laivy.serializable.utilities.Classes.getFields;

final class FieldContextFactory implements ContextFactory {

    // Static initializers

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

    public static <E> @Nullable E javaDeserializeObject(@NotNull Class<?> reference, @NotNull ArrayContext context) throws MalformedClassException, EOFException {
        if (context.isNullContext()) {
            return null;
        }

        // Byte array
        byte[] bytes;

        bytes = new byte[context.size()];

        for (int row = 0; row < bytes.length; row++) {
            bytes[row] = context.readByte();
            row++;
        }

        // Deserialize using object input stream
        try {
            // Read input stream
            @NotNull ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            //noinspection unchecked
            return (E) stream.readObject();
        } catch (@NotNull IOException | @NotNull ClassNotFoundException e) {
            throw new RuntimeException("an unknown error occurred trying to deserialize reference '" + reference + "' with json data '" + context + "'", e);
        }
    }
    public static @NotNull ArrayContext javaSerializeObject(@NotNull Serializer serializer, @Nullable SerializationProperties properties, @NotNull Object object) {
        try {
            @NotNull ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            @NotNull ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(object);

            // Byte array adapter
            @NotNull ArrayContext context = ArrayContext.create(serializer, properties);

            for (byte b : bytes.toByteArray()) {
                context.write(b);
            }

            return context;
        } catch (@NotNull IOException e) {
            throw new RuntimeException("cannot serialize java object '" + object + "' from class '" + object.getClass().getName() + "'", e);
        }
    }

    // Object

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        @NotNull Class<?> reference = object.getClass();

        // Using adapter
        if (properties != null && properties.getAdapter() != null) {
            return properties.getAdapter().write(object, serializer, properties);
        }

        // Check if this class should be serialized using Java's Serializer
        if (usesJavaSerialization(reference)) {
            return javaSerializeObject(serializer, properties, object);
        }

        // Retrieve fields
        @NotNull Map<String, Field> fields;

        if (properties != null) {
            @Nullable Father father = properties.getFather();
            fields = getFields(father, reference);

            // Check transients and bypass
            if (!properties.isBypassTransients()) {
                fields.values().removeIf(field -> Modifier.isTransient(field.getModifiers()));
            }

            // Removed non-included fields
            fields.values().removeIf(field -> !properties.getIncludedFields().contains(field));
        } else {
            fields = getFields(null, reference);
        }

        // Generate map context and write into it
        @NotNull MapContext context = MapContext.create(serializer, properties);

        for (@NotNull Entry<String, Field> entry : fields.entrySet()) {
            @NotNull String name = entry.getKey();
            @NotNull Field field = entry.getValue();

            if (field.getName().equals("this$0")) {
                continue;
            }

            context.setObject(name, Allocator.getFieldValue(field, object));
        }

        // Finish
        return context;
    }
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException, InstantiationException, InvalidClassException {
        @Nullable SerializationProperties properties = context.getProperties();

        // Using adapter
        if (properties != null && properties.getAdapter() != null) {
            return properties.getAdapter().read(reference, context);
        }

        // Deserialize normally
        if (context.isNullContext()) {
            return null;
        } else if (usesJavaSerialization(reference)) {
            if (!context.isArrayContext()) {
                throw new IllegalStateException("the reference '" + reference.getName() + "' uses java native serialization, but the context isn't iterable. Is missing any adapter?");
            }

            return javaDeserializeObject(reference, context.getAsArrayContext());
        } else if (context.isMapContext()) {
            // Variables
            @NotNull InstanceFactory instanceFactory = properties != null ? properties.getInstanceFactory() : InstanceFactory.allocator();
            @NotNull ContextFactory contextFactory = properties != null ? properties.getContextFactory() : ContextFactory.field();
            @Nullable Father father = properties != null ? properties.getFather() : null;

            // Serialize
            @NotNull Object instance = instanceFactory.generate(reference);

            @NotNull MapContext object = context.getAsMapContext();
            @NotNull Map<String, Field> fields = getFields(father, reference);

            // Process fields
            for (@NotNull String name : object.keySet()) {
                @Nullable Field field = fields.getOrDefault(name, null);

                if (field == null) {
                    throw new InvalidClassException("there's no field with name '" + name + "'");
                } else if (field.getName().startsWith("this$0") && field.isSynthetic()) {
                    if (properties == null || properties.getOuterInstance() == null) {
                        throw new NullPointerException("this class is not static, the outer instance must be defined at the serializing properties!");
                    } else if (!field.getType().isAssignableFrom(properties.getOuterInstance().getClass())) {
                        throw new IllegalArgumentException("this outer instance isn't the same outer class from this inner object");
                    }

                    // Set outer field instance
                    Allocator.setFieldValue(field, instance, properties.getOuterInstance());
                } else {
                    // Set normal field instance
                    @Nullable Object value = object.getObject(Classes.getReferences(field), name);
                    Allocator.setFieldValue(field, instance, value);
                }
            }

            // Finish
            return instance;
        } else {
            throw new IllegalStateException("the reference object '" + reference.getName() + "' is missing adapters");
        }
    }

}
