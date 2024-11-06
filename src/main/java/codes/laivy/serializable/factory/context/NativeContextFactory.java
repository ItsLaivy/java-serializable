package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.*;
import codes.laivy.serializable.exception.IllegalConcreteTypeException;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.exception.MissingOuterClassException;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static codes.laivy.serializable.config.Config.Father;
import static codes.laivy.serializable.utilities.Classes.getFields;
import static codes.laivy.serializable.utilities.Classes.isConcrete;

public final class NativeContextFactory implements ContextFactory {

    @Override
    public @Nullable Object write(@NotNull Class<?> reference, @Nullable Object object, @NotNull Serializer serializer, @NotNull Config config) {
        @Nullable Adapter adapter = serializer.getAdapter(reference).orElse(null);

        if (adapter != null) {
            return adapter.write(reference, object, serializer, config);
        } else if (object == null) {
            return null;
        }

        // Array block
        {
            if (reference.isArray()) {
                @NotNull Class<?> component = reference.getComponentType();
                @NotNull ArrayContext context = ArrayContext.create(serializer);
                int length = Array.getLength(object);

                for (int index = 0; index < length; index++) {
                    context.write(serializer.serialize(Array.get(object, index)));
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

        // Call #writeReplace method
        try {
            object = Classes.callWriteReplace(object, config.isIgnoreCasting());

            if (object == null) {
                return null;
            } else if (object.getClass() != reference) {
                return write(object.getClass(), object, serializer, config);
            }
        } catch (@NotNull NoSuchMethodException ignore) {
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

            @Nullable Object value = Allocator.getFieldValue(field, object);
            @NotNull Context fieldContext;

            if (value != null) {
                @NotNull Config fieldConfig = Config.builder(serializer, value.getClass(), Father.create(field, object)).build();
                fieldContext = serializer.toContext(value, fieldConfig);
            } else {
                fieldContext = NullContext.create();
            }

            context.setContext(name, fieldContext);
        }

        // Finish
        return context;
    }
    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Object read(@NotNull Class<?> main, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws IOException, InstantiationException {
        for (@NotNull Class<?> reference : config.getTypes()) try {
            @Nullable Adapter adapter = serializer.getAdapter(reference).orElse(null);

            if (adapter != null) {
                return adapter.read(reference, serializer, context, config);
            } else if (!isConcrete(reference)) {
                continue;
            } else if (context.isNull()) {
                return null;
            }

            // Read
            if (context.isMap()) {
                // Variables
                @NotNull InstanceFactory instanceFactory = config.getInstanceFactory();
                @Nullable Father father = config.getFather();

                // Serialize
                @Nullable Object instance = instanceFactory.generate(reference);

                @NotNull MapContext object = context.getAsMap();
                @NotNull Map<String, Field> fields = getFields(father, reference);

                // Outer
                @Nullable Field outer = Classes.getOuterClassField(reference);

                if (outer != null) {
                    if (config.getOuterInstance() == null) {
                        if (father != null) {
                            if (outer.getType().isAssignableFrom(father.getInstance().getClass())) {
                                config.setOuterInstance(father.getInstance());
                            }
                        } else {
                            throw new MissingOuterClassException("missing outer class configuration!");
                        }
                    }

                    if (config.getOuterInstance() != null) {
                        Allocator.setFieldValue(outer, instance, config.getOuterInstance());
                    }
                }

                // Process fields
                fields:
                for (@NotNull String name : object.keySet()) {
                    @Nullable Field field = fields.getOrDefault(name, null);

                    if (field == null) {
                        throw new IncompatibleReferenceException("there's no field with name '" + name + "' at class '" + reference.getName() + "': " + config);
                    } else if (object.getContext(name).isNull()) {
                        // Set outer field instance
                        Allocator.setFieldValue(field, instance, null);
                    } else {
                        if (field.getName().startsWith("this$0") && field.isSynthetic()) {
                            if (config.getOuterInstance() == null) {
                                throw new NullPointerException("this class is not static, the outer instance must be defined at the config: " + config);
                            } else if (!field.getType().isAssignableFrom(config.getOuterInstance().getClass())) {
                                throw new IllegalArgumentException("this outer instance isn't the same outer class from this inner object: " + config);
                            }

                            // Set outer field instance
                            Allocator.setFieldValue(field, instance, config.getOuterInstance());
                            continue;
                        }

                        @NotNull Set<Class<?>> references = Classes.getReferences(field);

                        for (@NotNull Class<?> fieldReference : references) {
                            config = Config.builder(serializer, fieldReference, Father.create(field, instance)).build();

                            try {
                                // Set normal field instance
                                @Nullable Object value = object.getObject(fieldReference, name, config);
                                Allocator.setFieldValue(field, instance, value);

                                continue fields;
                            } catch (@NotNull IllegalConcreteTypeException | @NotNull IncompatibleReferenceException ignore) {
                            }
                        }

                        throw new IncompatibleReferenceException("cannot deserialize field '" + field + "' because there's no compatible references for it: " + references + ", configuration: " + config);
                    }
                }

                // Call #readResolve method
                try {
                    instance = Classes.callReadResolve(instance, config.isIgnoreCasting());
                } catch (@NotNull NoSuchMethodException ignore) {
                }

                // Finish
                return instance;
            } else if (context.isArray()) {
                // Check if it uses java serialization
                if (Classes.usesJavaSerialization(reference)) {
                    try {
                        return Classes.javaDeserializeObject(reference, context.getAsArray());
                    } catch (@NotNull EOFException e) {
                        throw new RuntimeException("problems trying to deserialize using java native serialization. Is it missing any adapter?", e);
                    } catch (@NotNull StreamCorruptedException e) {
                        throw new RuntimeException("stream array corrupted. Is it missing any adapter?", e);
                    } catch (@NotNull IOException e) {
                        throw new RuntimeException(e);
                    } catch (@NotNull ClassNotFoundException e) {
                        throw new RuntimeException("cannot find class reference", e);
                    }
                }

                // Create the array context
                @NotNull ArrayContext array = context.getAsArray();
                int size = array.size();

                if (!reference.isArray()) {
                    throw new IncompatibleReferenceException("this reference '" + reference + "' must be an array or it's missing adapters: " + config);
                } else {
                    @NotNull Class<?> component = reference.getComponentType();
                    @NotNull Object object = Array.newInstance(component, size);

                    for (int row = 0; row < size; row++) {
                        Array.set(object, row, array.readObject(component));
                    }

                    return object;
                }
            } else if (context.isPrimitive()) {
                @NotNull PrimitiveContext primitive = context.getAsPrimitive();

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
        } catch (@NotNull IncompatibleReferenceException ignore) {
        }

        if (config.getTypes().isEmpty() || !isConcrete(main)) {
            throw new IllegalConcreteTypeException("there's no concrete type for reference '" + main.getName() + "'. Is it missing any adapters? - " + context + ", configuration: " + config);
        } else {
            throw new IncompatibleReferenceException("there's no compatible reference to read object: " + config);
        }
    }

}
