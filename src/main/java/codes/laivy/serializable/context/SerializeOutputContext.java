package codes.laivy.serializable.context;

import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Provides a context for serializing an object. This context encapsulates
 * the necessary utilities for writing field values to the output during the serialization process.
 * It supports writing both primitive types and objects, including those that implement the
 * {@link Serializable} interface, ensuring that all serializable fields are handled correctly.
 * <p>
 * The primary goal of this interface is to abstract the complexities of serializing objects
 * by associating field names with their corresponding values, ensuring an organized and structured
 * output. It allows for precise control over the serialization process, ensuring that all necessary
 * field data is recorded in a manner consistent with the target format or protocol.
 * <p>
 * This interface is designed for flexibility, enabling the serialization of any object, while
 * maintaining the ability to enforce constraints and handle special cases for fields that may
 * not be directly serializable. Implementations are expected to handle exceptions that may arise
 * from invalid data or unsupported types.
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Experimental
public interface SerializeOutputContext {

    /**
     * Returns the reference class for the object being serialized. This method provides
     * access to the {@link Class} of the object type currently being serialized. It can be
     * useful for reflection-based serialization mechanisms or when performing type checks
     * during serialization.
     * <p>
     * The class information can help in scenarios where the serialization behavior depends on
     * the specific class structure, such as when applying custom serialization strategies
     * based on the object's type.
     *
     * @return The {@link Class} representing the type currently being serialized.
     */
    @NotNull Class<?> getReference();

    /**
     * Writes a field value to the serialization context. The field is identified by its
     * name, and the corresponding value can be any object. If the object is not serializable
     * directly, the serialization mechanism must handle this case, such as by throwing an exception
     * or by applying a custom serialization strategy.
     * <p>
     * This method is the general-purpose method for writing fields of any type. It is expected that
     * implementations can handle both standard Java types and user-defined objects.
     * Special attention must be given to fields that are {@code null} or that represent complex
     * object graphs to ensure correct serialization.
     *
     * @param name  The name of the field being serialized. This is typically the field's
     *              identifier in the class definition.
     * @param value The value of the field being serialized. This value can be any object
     *              or {@code null} if the field is not set or has no value.
     * @throws IllegalArgumentException If the name is {@code null}, or if the serialization
     *                                  constraints are violated (e.g., unsupported field types).
     */
    void write(@NotNull String name, @Nullable Object value);

    /**
     * Writes a serializable field value to the serialization context. This method is specialized
     * for handling objects that implement {@link Serializable}. It ensures that the provided object
     * adheres to the contract of the {@link Serializable} interface, guaranteeing that the value
     * can be serialized without violating the constraints of standard Java serialization.
     * <p>
     * Implementations of this method must verify that the object passed in is fully serializable.
     * If any part of the object graph is not serializable, an appropriate exception should be
     * thrown or a suitable fallback strategy must be applied.
     *
     * @param name         The name of the field being serialized.
     * @param serializable The serializable value of the field being serialized. This must
     *                     implement the {@link Serializable} interface.
     * @throws IllegalArgumentException If the name is {@code null}, the value is not serializable,
     *                                  or if serialization constraints are violated.
     */
    void write(@NotNull String name, @Nullable Serializable serializable);

    /**
     * Writes a boolean value to the serialization context.
     *
     * @param bool The boolean value to be serialized.
     */
    void write(boolean bool);

    /**
     * Writes a byte value to the serialization context.
     *
     * @param b The byte value to be serialized.
     */
    void write(byte b);

    /**
     * Writes a short value to the serialization context.
     *
     * @param s The short value to be serialized.
     */
    void write(short s);

    /**
     * Writes a char value to the serialization context.
     *
     * @param c The char value to be serialized.
     */
    void write(char c);

    /**
     * Writes an int value to the serialization context.
     *
     * @param i The int value to be serialized.
     */
    void write(int i);

    /**
     * Writes a long value to the serialization context.
     *
     * @param l The long value to be serialized.
     */
    void write(long l);

    /**
     * Writes a float value to the serialization context.
     *
     * @param f The float value to be serialized.
     */
    void write(float f);

    /**
     * Writes a double value to the serialization context.
     *
     * @param d The double value to be serialized.
     */
    void write(double d);

    /**
     * Writes a string value to the serialization context. If the string is {@code null},
     * it should be handled accordingly (e.g., written as a null value or skipped, depending
     * on the serialization strategy).
     *
     * @param string The string value to be serialized, or {@code null}.
     */
    void write(@Nullable String string);

    /**
     * Writes a generic object to the serialization context. This method can be used when
     * the type of the object is not known at compile-time. It is expected that the object
     * provided either implements {@link Serializable} or is handled via a custom serialization
     * mechanism.
     *
     * @param object The object to be serialized, or {@code null}.
     */
    void write(@Nullable Object object);

    @NotNull JsonSerializer getSerializer();

}