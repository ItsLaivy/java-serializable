package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.UseEmptyConstructor;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.ArrayContext;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.MapContext;
import codes.laivy.serializable.context.PrimitiveContext;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import static codes.laivy.serializable.config.Config.Father;
import static codes.laivy.serializable.utilities.Classes.getFields;

public final class NativeContextFactory implements ContextFactory {

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @NotNull Config config) {
        @NotNull Class<?> reference = object.getClass();

        // Array block
        {
            if (reference.isArray()) {
                @NotNull Class<?> component = reference.getComponentType();
                @NotNull ArrayContext context = ArrayContext.create(serializer);
                int length = Array.getLength(object);

                for (int index = 0; index < length; index++) {
                    context.write(serializer.serialize(Array.get(object, index), config));
                }

                return context;
            }
        }

        // Primitive block
        {
            if (reference.isEnum()) {
                return PrimitiveContext.create(((Enum<?>) object).name());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Boolean.class, reference)) {
                return PrimitiveContext.create((Boolean) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Short.class, reference)) {
                return PrimitiveContext.create((Short) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Integer.class, reference)) {
                return PrimitiveContext.create((Integer) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Long.class, reference)) {
                return PrimitiveContext.create((Long) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Float.class, reference)) {
                return PrimitiveContext.create((Float) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Double.class, reference)) {
                return PrimitiveContext.create((Double) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Character.class, reference)) {
                return PrimitiveContext.create((Character) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(Byte.class, reference)) {
                return PrimitiveContext.create((Byte) object);
            } else if (Allocator.isAssignableFromIncludingPrimitive(String.class, reference)) {
                return PrimitiveContext.create((String) object);
            }
        }

        // Java serialization block
        {
            if (Classes.usesJavaSerialization(reference)) {
                return Classes.javaSerializeObject(serializer, object);
            }
        }
        
        // Retrieve fields
        @Nullable Father father = config.getFather();
        @NotNull Map<String, Field> fields = getFields(father, reference);

        // Check transients and bypass
        if (!config.isBypassTransients()) {
            fields.values().removeIf(field -> Modifier.isTransient(field.getModifiers()));
        }

        // Removed non-included fields
        fields.values().removeIf(field -> !config.getIncludedFields().contains(field));

        // Generate map context and write into it
        @NotNull MapContext context = MapContext.create(serializer);

        for (@NotNull Entry<String, Field> entry : fields.entrySet()) {
            @NotNull String name = entry.getKey();
            @NotNull Field field = entry.getValue();

            if (field.getName().equals("this$0")) {
                continue;
            }

            @NotNull Config fieldConfig = Config.create(serializer, Father.create(field, object));
            @NotNull Context t = serializer.toContext(Allocator.getFieldValue(field, object), fieldConfig);

            context.setContext(name, t);
        }

        // Finish
        return context;
    }
    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws EOFException, InstantiationException, InvalidClassException {
        // Deserialize normally
        if (context.isNullContext()) {
            return null;
        } else if (context.isMapContext()) {
            // Variables
            @NotNull InstanceFactory instanceFactory = config.getInstanceFactory();
            @Nullable Father father = config.getFather();

            if (reference.isAnnotationPresent(UseEmptyConstructor.class)) {
                instanceFactory = InstanceFactory.constructor();
            }

            // Serialize
            @NotNull Object instance = instanceFactory.generate(reference);

            @NotNull MapContext object = context.getAsMapContext();
            @NotNull Map<String, Field> fields = getFields(father, reference);

            // Process fields
            fields:
            for (@NotNull String name : object.keySet()) {
                @Nullable Field field = fields.getOrDefault(name, null);

                if (field == null) {
                    throw new InvalidClassException("there's no field with name '" + name + "'");
                } else {
                    config = Config.create(serializer, Father.create(field, instance));

                    if (field.getName().startsWith("this$0") && field.isSynthetic()) {
                        if (config.getOuterInstance() == null) {
                            throw new NullPointerException("this class is not static, the outer instance must be defined at the serializing properties!");
                        } else if (!field.getType().isAssignableFrom(config.getOuterInstance().getClass())) {
                            throw new IllegalArgumentException("this outer instance isn't the same outer class from this inner object");
                        }

                        // Set outer field instance
                        Allocator.setFieldValue(field, instance, config.getOuterInstance());
                    } else {
                        @NotNull Class<?>[] references = Classes.getReferences(field);

                        for (@NotNull Class<?> fieldReference : references) try {
                            // Set normal field instance
                            @Nullable Object value = object.getObject(fieldReference, name);
                            Allocator.setFieldValue(field, instance, value);

                            continue fields;
                        } catch (@NotNull IncompatibleReferenceException ignore) {
                        }

                        throw new IncompatibleReferenceException("cannot deserialize field '" + field + "' because there's no compatible and concrete references for it: " + Arrays.toString(references));
                    }
                }
            }

            // Finish
            return instance;
        } else if (context.isArrayContext()) {
            // Check if it uses java serialization
            if (Classes.usesJavaSerialization(reference)) {
                if (context.isArrayContext()) try {
                    return Classes.javaDeserializeObject(reference, context.getAsArrayContext());
                } catch (@NotNull EOFException e) {
                    throw new RuntimeException("problems trying to deserialize using java native serialization. Is it missing any adapter?", e);
                } catch (@NotNull StreamCorruptedException e) {
                    throw new RuntimeException("stream array corrupted. Is it missing any adapter?", e);
                } catch (@NotNull IOException e) {
                    throw new RuntimeException(e);
                } catch (@NotNull ClassNotFoundException e) {
                    throw new RuntimeException("cannot find class reference", e);
                } else {
                    throw new IllegalStateException("cannot deserialize using java native serialization. Is it missing any adapter?");
                }
            }

            // Create the array context
            @NotNull ArrayContext array = context.getAsArrayContext();
            int size = array.size();

            if (!reference.isArray()) {
                throw new IllegalArgumentException("this reference must be an array or it's missing adapters.");
            } else {
                @NotNull Class<?> component = reference.getComponentType();
                @NotNull Object object = Array.newInstance(component, size);

                for (int row = 0; row < size; row++) {
                    Array.set(object, row, array.readObject(component));
                }

                return object;
            }
        } else if (context.isPrimitiveContext()) {
            @NotNull PrimitiveContext primitive = context.getAsPrimitiveContext();

            if (reference.isEnum()) {
                //noinspection rawtypes
                return Enum.valueOf((Class) reference, primitive.getAsString());
            } else if (Allocator.isAssignableFromIncludingPrimitive(Boolean.class, reference)) {
                return primitive.getAsBoolean();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Short.class, reference)) {
                return primitive.getAsShort();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Integer.class, reference)) {
                return primitive.getAsInteger();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Long.class, reference)) {
                return primitive.getAsLong();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Float.class, reference)) {
                return primitive.getAsFloat();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Double.class, reference)) {
                return primitive.getAsDouble();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Character.class, reference)) {
                return primitive.getAsCharacter();
            } else if (Allocator.isAssignableFromIncludingPrimitive(Byte.class, reference)) {
                return primitive.getAsByte();
            } else if (Allocator.isAssignableFromIncludingPrimitive(String.class, reference)) {
                return primitive.getAsString();
            }
        }

        throw new IllegalStateException("the reference object '" + reference.getName() + "' (" + Allocator.isAssignableFromIncludingPrimitive(Integer.class, reference) + ") is missing adapters: " + context);
    }

}
