package codes.laivy.serializable.context;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Provides a context for serializing an object of type {@link T}. This context encapsulates
 * the necessary utilities for writing field values to the output during the serialization process.
 * It supports writing both regular objects and serializable objects into the serialization stream.
 * <p>
 * The primary purpose of this interface is to handle serialization by associating field names with
 * their corresponding values, allowing structured and organized output.
 *
 * @param <T> The type of object being serialized.
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Experimental
public interface SerializeOutputContext<T> {

    /**
     * Returns the reference class for the object being serialized. This method provides
     * access to the {@link Class} of the object type {@link T} being serialized, which can
     * be useful for reflection and type checks during serialization.
     *
     * @return The {@link Class} representing the type {@link T} currently being serialized.
     */
    @NotNull Class<T> getReference();

    /**
     * Writes a field value to the serialization context. The field is identified by its
     * name, and the corresponding value can be any object. If the object is not serializable
     * directly, the serialization mechanism must handle this case accordingly.
     *
     * @param name  The name of the field being serialized. This is typically the field's
     *              identifier in the class definition.
     * @param value The value of the field being serialized. This value can be any object
     *              or {@code null} if the field is not set or has no value.
     * @throws IllegalArgumentException If the name is {@code null} or if serialization
     *                                  constraints are violated.
     */
    void write(@NotNull String name, @Nullable Object value);

    /**
     * Writes a serializable field value to the serialization context. This method is specialized
     * for handling objects that implement {@link Serializable}, ensuring that the value can be
     * serialized in accordance with the {@link Serializable} contract.
     *
     * @param name         The name of the field being serialized.
     * @param serializable The serializable value of the field being serialized. This must
     *                     implement the {@link Serializable} interface.
     * @throws IllegalArgumentException If the name is {@code null}, the value is not serializable,
     *                                  or if serialization constraints are violated.
     */
    void write(@NotNull String name, @Nullable Serializable serializable);
}