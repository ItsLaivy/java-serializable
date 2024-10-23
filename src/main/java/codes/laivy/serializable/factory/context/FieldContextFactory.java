package codes.laivy.serializable.factory.context;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.MapContext;
import codes.laivy.serializable.factory.instance.InstanceFactory;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.utilities.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;

import static codes.laivy.serializable.properties.SerializationProperties.Father;
import static codes.laivy.serializable.utilities.Classes.getFields;

public final class FieldContextFactory implements ContextFactory {

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        @NotNull Class<?> reference = object.getClass();

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

            // todo: esse objeto precisa ter seu próprio properties
            @NotNull SerializationProperties fieldProperties = SerializationProperties.create(serializer, field, object);
            @NotNull Context t = serializer.toContext(Allocator.getFieldValue(field, object), fieldProperties);

            context.setContext(name, t);
        }

        // Finish
        return context;
    }
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context) throws EOFException, InstantiationException, InvalidClassException {
        @Nullable SerializationProperties properties = context.getProperties();

        // Deserialize normally
        if (context.isNullContext()) {
            return null;
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
