package codes.laivy.serializable.context;

import codes.laivy.serializable.annotations.KnownAs;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Provides a context for deserializing an object of type {@link T}. This context encapsulates
 * various metadata and utilities needed to extract field values and manage the deserialization
 * process.
 * <p>
 * The context allows access to the fields of the serialized object, as well as metadata
 * about the class being deserialized. Additionally, it provides methods to deserialize the
 * object or extract individual field values.
 * <p>
 * This interface is designed to abstract the complexities involved in reconstructing objects
 * from a serialized format, allowing for seamless integration with various serialization
 * frameworks and protocols.
 *
 * @param <T> The type of object being deserialized.
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Experimental
public interface SerializeInputContext<T> {

    /**
     * Returns the reference class for the object being deserialized. This provides
     * information about the type {@link T}, which represents the type of the object
     * currently being deserialized in this context.
     * <p>
     * This method is useful for frameworks that need to inspect the structure or metadata of the
     * deserialized object before proceeding with the deserialization of specific fields.
     *
     * @return The {@link Class} representing the type {@link T} that is currently being deserialized.
     */
    @NotNull Class<T> getReference();

    /**
     * Reads a boolean value from the input context. This method reads the next
     * boolean field in the serialized data.
     *
     * @return The boolean value read from the context.
     */
    boolean readBoolean();

    /**
     * Reads a byte value from the input context. This method reads the next
     * byte field in the serialized data.
     *
     * @return The byte value read from the context.
     */
    byte readByte();

    /**
     * Reads a short value from the input context. This method reads the next
     * short field in the serialized data.
     *
     * @return The short value read from the context.
     */
    short readShort();

    /**
     * Reads a char value from the input context. This method reads the next
     * char field in the serialized data.
     *
     * @return The char value read from the context.
     */
    char readChar();

    /**
     * Reads an int value from the input context. This method reads the next
     * int field in the serialized data.
     *
     * @return The int value read from the context.
     */
    int readInt();

    /**
     * Reads a long value from the input context. This method reads the next
     * long field in the serialized data.
     *
     * @return The long value read from the context.
     */
    long readLong();

    /**
     * Reads a float value from the input context. This method reads the next
     * float field in the serialized data.
     *
     * @return The float value read from the context.
     */
    float readFloat();

    /**
     * Reads a double value from the input context. This method reads the next
     * double field in the serialized data.
     *
     * @return The double value read from the context.
     */
    double readDouble();

    /**
     * Reads a line of text from the input context. This method reads the next
     * string value in the serialized data.
     * <p>
     * If the value is {@code null}, the method should handle this gracefully, either
     * by returning {@code null} or throwing an appropriate exception depending on
     * the context requirements.
     *
     * @return The string value read from the context, or {@code null} if the value is not available.
     */
    @Nullable String readLine();

    /**
     * Reads a generic object from the input context. The object type is dynamically determined
     * based on the serialized data.
     * <p>
     * This method is flexible and can handle various types, including user-defined objects,
     * arrays, and other complex data structures.
     *
     * @param <E> The type of object being deserialized.
     * @return The deserialized object, or {@code null} if the value is not available.
     */
    <E> @Nullable E readObject();

    /**
     * Retrieves the {@link FieldData} for a given field by its name. This method provides access
     * to metadata related to a specific field of the object, such as its {@link java.lang.reflect.Field}
     * and the current value of the field (if available).
     * <p>
     * The {@link FieldData} can be used to inspect the field's properties, including its name,
     * type, and annotations, and to retrieve its current value during deserialization.
     *
     * @param name The name of the field whose data is to be retrieved.
     * @return The {@link FieldData} object containing metadata about the requested field.
     * @throws IllegalArgumentException If the field with the specified name does not exist.
     */
    @NotNull FieldData getField(@NotNull String name);

    /**
     * Retrieves the {@link FieldData} for a given {@link Field}. This method provides access
     * to the field's metadata based on the {@link Field} object itself, allowing for reflection-based
     * deserialization of specific fields.
     *
     * @param field The {@link Field} object representing the field.
     * @return The {@link FieldData} object containing metadata about the requested field.
     * @throws IllegalArgumentException If the field does not exist in the current deserialization context.
     */
    @NotNull FieldData getField(@NotNull Field field);

    /**
     * Retrieves the current value of a field by its name. This method allows access to the
     * deserialized value of a specific field from the input context.
     *
     * @param name The name of the field whose value is to be retrieved.
     * @return The deserialized value of the field, or {@code null} if the value is not set or available.
     * @throws IllegalArgumentException If the field with the specified name does not exist.
     */
    @Nullable Object getValue(@NotNull String name);

    /**
     * Returns an array of {@link FieldData} representing all fields present in the object
     * being deserialized. This method can be used to iterate over all fields of the object
     * and access their metadata.
     *
     * @return An array of {@link FieldData} objects, each representing a field in the object.
     */
    @NotNull FieldData @NotNull [] getFields();

    /**
     * Performs the deserialization of the object of type {@link T} in this context.
     * This method will reconstruct the object using the current data provided in the
     * input context.
     * <p>
     * It is expected that the method will handle all necessary field value extraction
     * and object construction, ensuring the returned object is fully populated with
     * the appropriate deserialized data.
     *
     * @return The deserialized object of type {@link T}.
     */
    @NotNull T deserialize();

    /**
     * Represents metadata and information about a single field within the object being
     * deserialized. This interface provides methods to access the underlying field
     * information and retrieve the field's value.
     * <p>
     * This interface abstracts the reflection and type-handling logic required for
     * interacting with fields during deserialization, ensuring that the field's
     * metadata and value can be easily accessed.
     */
    interface FieldData {

        /**
         * Returns the underlying {@link Field} object that represents this field in the
         * deserialized object. The {@link Field} object provides reflection-based access
         * to the field's properties, such as its type, name, and modifiers.
         *
         * @return The {@link Field} object representing this field.
         */
        @NotNull Field getField();

        /**
         * Returns the declaring class of this field. By default, this method uses the
         * {@link Field#getDeclaringClass()} method to fetch the class where the field
         * is defined.
         *
         * @return The {@link Class} object that declares this field.
         */
        default @NotNull Class<?> getReference() {
            return getField().getDeclaringClass();
        }

        /**
         * Returns the original name, without the {@link KnownAs} and repeated field patterns.
         * This method provides access to the unmodified, original name of the field as
         * defined in the class declaration.
         *
         * @return The original name of the field.
         */
        default @NotNull String getOriginalName() {
            return getField().getName();
        }

        /**
         * Returns the name of the field. The field name is the identifier used to
         * refer to this field in the class definition and during serialization or deserialization.
         *
         * @return The name of the field.
         */
        @NotNull String getName();

        /**
         * Returns the current value of the field. If the field has been deserialized,
         * this method will return the deserialized value. If the field has not been deserialized
         * or its value is unset, this method may return {@code null}.
         *
         * @return The value of the field, or {@code null} if the value is not available or unset.
         */
        @Nullable Object getValue();
    }
}