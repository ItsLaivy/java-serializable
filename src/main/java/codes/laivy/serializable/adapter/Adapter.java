package codes.laivy.serializable.adapter;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.json.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;

/**
 * The `Adapter` interface defines a contract for serializing and deserializing objects of a specific type `O`,
 * where the type represents the serialized form `T`.
 * This interface is designed to provide a custom
 * serialization mechanism that can be more human-readable and functional than Java's native serialization methods.
 *
 * <p>Each {@link Serializer} have a list of default adapters. These adapters are
 * particularly useful in cases where the default Java serialization (e.g., via {@code readObject} and
 * {@code writeObject} methods) produces output that is not aesthetically pleasing or lacks necessary features
 * for certain classes. By using an adapter, you can ensure that these classes are serialized in a more
 * controlled and customized manner.</p>
 *
 * <p>Without adapters, classes relying on native Java serialization methods are serialized directly using their
 * raw bytes. This approach can result in serialized data that is difficult to read or lacks clarity. To enhance
 * the readability and functionality of serialized data, especially for complex classes, it is advisable to create
 * a custom adapter that handles both serialization and deserialization.</p>
 *
 * <p>This interface provides two key methods: {@code serialize} and {@code deserialize}. The {@code serialize}
 * method converts an object of type `O` into its serialized form `T`. Conversely, the {@code deserialize} method
 * converts an object of type `T` back into its original form `O`. These methods may throw an {@link InvalidClassException}
 * if the class of the object being serialized or deserialized is not compatible with the expected types.</p>
 *
 * @param <T> The type representing the serialized form of the object.
 * @param <O> The type of the object to be serialized and deserialized.
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public interface Adapter<T, O> {

    /**
     * Retrieves the class reference of the type `O` that this adapter is designed to handle.
     *
     * @return A non-null {@link Class} object representing the type `O`.
     */
    @NotNull Class<O> getReference();

    /**
     * Serializes an object of type `O` into its corresponding serialized form `T`.
     *
     * @param serializer The serializer instance.
     * @param instance The object to be serialized, which may be null.
     * @return The serialized form of the object, or null if the object was null.
     * @throws InvalidClassException If this adapter cannot serialize the class of the object.
     */
    @Nullable T serialize(@NotNull JsonSerializable serializer, @Nullable O instance) throws InvalidClassException;

    /**
     * Deserializes an object of type `T` back into its original form `O`.
     *
     * @param serializer The serializer instance.
     * @param object The serialized object to be deserialized, which may be null.
     * @param reference The object class that wants to be deserialized
     * @return The original object of type `O`, or null if the serialized object was null.
     * @throws InvalidClassException If the class of the serialized object is not compatible with type `O`.
     */
    @Nullable O deserialize(@NotNull JsonSerializable serializer, @NotNull Class<O> reference, @Nullable T object) throws InvalidClassException;

}

