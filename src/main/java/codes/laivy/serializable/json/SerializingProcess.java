package codes.laivy.serializable.json;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.*;
import codes.laivy.serializable.exception.NullConcreteClassException;
import codes.laivy.serializable.json.SerializingType.Methods;
import codes.laivy.serializable.json.SerializingType.Normal;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import static codes.laivy.serializable.json.SerializingType.Normal.getFields;

final class SerializingProcess {

    // Static initializers

    public static boolean isConcrete(@NotNull Class<?> reference) {
        if (reference.isArray()) {
            return isConcrete(reference.getComponentType());
        } else if (reference.isPrimitive()) {
            return true;
        }

        return !Modifier.isInterface(reference.getModifiers()) && !Modifier.isAbstract(reference.getModifiers());
    }

    // Object

    private final @NotNull JsonSerializer serializer;

    private final @Nullable Father father;
    private final @NotNull Class<?> reference;

    public SerializingProcess(@NotNull JsonSerializer serializer, @NotNull Class<?> reference) {
        if (!isConcrete(reference)) {
            throw new NullConcreteClassException("the class '" + reference + "' cannot be deserialized since it's not concrete.");
        }

        this.serializer = serializer;
        this.father = null;
        this.reference = reference;
    }
    public SerializingProcess(@NotNull JsonSerializer serializer, @NotNull Father father) {
        this.serializer = serializer;
        this.father = father;
        this.reference = checkConcrete(father);
    }

    // Getters

    /**
     * Esse getter retorna o field e a instância (desse field, para usar no Field#get) pai dessa operação, caso o objeto que deve ser deserializado não veio de uma chamada interna de um campo
     * (caso não queira deserializar o valor de um campo) esse método retornará nulo
     */
    public @Nullable Father getFather() {
        return father;
    }

    public @NotNull Class<?> getReference() {
        return reference;
    }

    // Modules

    public @Nullable JsonElement serialize(@Nullable Object instance) {
        if (instance == null) {
            return null;
        } else if (reference != instance.getClass()) {
            throw new IllegalArgumentException();
        }

        // Get fields
        @NotNull Collection<Field> fields = getFields(father, reference).values();

        // Start
        boolean bypassTransients = false;
        @NotNull SerializingType serializing = new Normal(serializer, father);

        if (reference.isAnnotationPresent(UsingSerializers.class)) {
            serializing = new Methods(serializer, father, reference, reference.getAnnotation(UsingSerializers.class));
        } else if (serializer.adapterMap.containsKey(reference)) {
            @NotNull JsonSerializeOutputContext context = new JsonSerializeOutputContext(serializer, instance.getClass());
            serializer.adapterMap.get(reference).serialize(instance, context);

            return context.serialize();
        } else if (JavaSerializableUtils.usesJavaSerialization(reference)) {
            return JavaSerializableUtils.javaSerializeObject(instance);
        } else if (instance instanceof Enum<?>) {
            return serializer.serialize((Enum<?>) instance);
        } else if (instance instanceof Enum<?>[]) {
            return serializer.serialize((Enum<?>[]) instance);
        } else if (instance instanceof Boolean) {
            return serializer.serialize((Boolean) instance);
        } else if (instance instanceof Boolean[]) {
            return serializer.serialize((Boolean[]) instance);
        } else if (instance instanceof Character) {
            return serializer.serialize((Character) instance);
        } else if (instance instanceof Character[]) {
            return serializer.serialize((Character[]) instance);
        } else if (instance instanceof Byte) {
            return serializer.serialize((Byte) instance);
        } else if (instance instanceof Byte[]) {
            return serializer.serialize((Byte[]) instance);
        } else if (instance instanceof Integer) {
            return serializer.serialize((Integer) instance);
        } else if (instance instanceof Short) {
            return serializer.serialize((Short) instance);
        } else if (instance instanceof Long) {
            return serializer.serialize((Long) instance);
        } else if (instance instanceof Float) {
            return serializer.serialize((Float) instance);
        } else if (instance instanceof Double) {
            return serializer.serialize((Double) instance);
        } else if (instance instanceof Number[]) {
            return serializer.serialize((Number[]) instance);
        } else if (instance instanceof Number) {
            return serializer.serialize((Number) instance);
        } else if (instance instanceof boolean[]) {
            return serializer.serialize((boolean[]) instance);
        } else if (instance instanceof char[]) {
            return serializer.serialize((char[]) instance);
        } else if (instance instanceof byte[]) {
            return serializer.serialize((byte[]) instance);
        } else if (instance instanceof int[]) {
            return serializer.serialize((int[]) instance);
        } else if (instance instanceof short[]) {
            return serializer.serialize((short[]) instance);
        } else if (instance instanceof long[]) {
            return serializer.serialize((long[]) instance);
        } else if (instance instanceof float[]) {
            return serializer.serialize((float[]) instance);
        } else if (instance instanceof double[]) {
            return serializer.serialize((double[]) instance);
        } else if (instance instanceof String) {
            return serializer.serialize((String) instance);
        } else if (instance instanceof String[]) {
            return serializer.serialize((String[]) instance);
        } else if (instance instanceof Object[]) {
            return serializer.serialize((Object[]) instance);
        }

        // Finish
        return serializing.serialize(instance);
    }
    public @Nullable Object deserialize(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        // Deserialize
        @NotNull SerializingType serializing = new Normal(serializer, father);

        if (reference.isAnnotationPresent(UsingSerializers.class)) {
            serializing = new Methods(serializer, father, reference, reference.getAnnotation(UsingSerializers.class));
        } else if (serializer.adapterMap.containsKey(reference)) try {
            @NotNull Adapter adapter = serializer.adapterMap.get(reference);
            @NotNull JsonSerializeInputContext<?> context = new JsonSerializeInputContext<>(serializer, reference, element);
            @NotNull Object object = adapter.deserialize(context);

            if (!reference.isAssignableFrom(object.getClass())) {
                throw new IllegalStateException("the adapter returned '" + object.getClass() + "' that isn't assignable from '" + reference + "'");
            }

            return object;
        } catch (@NotNull EOFException e) {
            throw new RuntimeException("cannot proceed adapter deserialization '" + reference + "': " + element, e);
        } else if (JavaSerializableUtils.usesJavaSerialization(reference)) {
            return JavaSerializableUtils.javaDeserializeObject(reference, element);
        }

        // Finish
        return serializing.deserialize(reference, element);
    }

    // Utilities

    public static @NotNull Class<?> checkConcrete(@NotNull Father father) {
        @NotNull Field field = father.getField();
        @NotNull Object instance = father.getInstance();

        // Annotations
        @Nullable OnlyFields only = field.isAnnotationPresent(OnlyFields.class) ? field.getAnnotation(OnlyFields.class) : null;
        @Nullable ExcludeFields exclude = field.isAnnotationPresent(ExcludeFields.class) ? field.getAnnotation(ExcludeFields.class) : null;
        @Nullable BypassTransient bypassTransient = field.isAnnotationPresent(BypassTransient.class) ? field.getAnnotation(BypassTransient.class) : null;

        // Check for concrete annotations
        boolean accessible = field.isAccessible();
        field.setAccessible(true);

        if (!isConcrete(field.getType())) {
            if (field.isAnnotationPresent(Concrete.class)) {
                @NotNull Concrete[] concretes = field.getAnnotationsByType(Concrete.class);

                for (@NotNull Concrete concrete : concretes) {
                    if (!isConcrete(concrete.type())) {
                        throw new IllegalArgumentException("the @Concrete argument must be a valid concrete class!");
                    }

                    if (checkCompatible()) {
                        return concrete.type();
                    }
                }

                throw new IllegalArgumentException("cannot find a valid compatible concrete type for field '" + field + "'");
            } else {
                try {
                    @Nullable Object temp = field.get(instance);

                    if (temp == null) {
                        throw new NullConcreteClassException("cannot retrieve concrete class from field '" + field + "'. Try to use @Concrete of a default value for the field.");
                    } else {
                        return temp.getClass();
                    }
                } catch (@NotNull IllegalAccessException e) {
                    throw new RuntimeException("cannot access field value for concrete check", e);
                } finally {
                    field.setAccessible(accessible);
                }
            }
        } else {
            return field.getType();
        }
    }
    public static boolean checkCompatible() {
        return true;
    }

}
