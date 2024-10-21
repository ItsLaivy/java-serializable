package codes.laivy.serializable.reference;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * The {@code References} interface represents a specialized collection of {@link Class} objects
 * that are used as possible reference types for deserialization purposes. This interface is
 * particularly useful when the exact type of serialized object is not fully known at the time
 * of serialization, and multiple candidate types need to be provided.
 * <p>
 * A typical use case for {@code References} arises when the original object being serialized could be
 * an instance of multiple different types, such as {@link String}, {@link Number}, or {@link UUID}.
 * By creating an instance of {@code References} that contains these potential types, the deserializer
 * can attempt to deserialize the object using the first compatible type in the collection.
 * <p>
 * Key features:
 * - **Multiple Type Support:** This collection allows for the specification of multiple reference types
 *   to handle ambiguity during deserialization.
 * - **Order Preservation:** The order of the elements is significant, as the deserializer will attempt
 *   to match the types in the same order they are provided.
 * - **No Duplicates:** The collection does not allow duplicate references, ensuring that each type is
 *   checked only once.
 * <p>
 * <strong>Usage Example</strong>:
 * Suppose you are serializing an object whose type could be {@code String}, {@code Number}, or
 * {@code UUID}. You can use the {@code References} interface to create a collection of these types:
 *
 * <pre>{@code
 * @NotNull References refs = References.of(String.class, Number.class, UUID.class);
 * }</pre>
 *
 * During deserialization, the system will use this collection to attempt deserialization in the
 * order specified, ensuring the first compatible type is selected.
 * <p>
 *
 * @see Collection
 * @see Class
 */
public interface References extends Collection<Class<?>> {

    /**
     * Creates a new {@code References} instance containing the specified classes.
     * This method allows you to specify multiple classes as potential references
     * for deserialization. The order in which the classes are passed will determine
     * the order in which they will be considered during deserialization.
     *
     * @param classes an array of {@link Class} objects representing the potential reference types
     * @return a new {@code References} instance containing the provided classes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    static @NotNull References of(@NotNull Class<?> @NotNull ... classes) {
        return of(Arrays.asList(classes));
    }

    /**
     * Creates a new {@code References} instance containing the classes from the specified collection.
     * The collection will be iterated in the order it is passed, preserving the sequence in which
     * the types will be checked during deserialization.
     *
     * @param classes a {@link Collection} of {@link Class} objects representing the potential reference types
     * @return a new {@code References} instance containing the classes from the provided collection
     * @throws NullPointerException if the collection or any of its elements are {@code null}
     */
    static @NotNull References of(@NotNull Collection<Class<?>> classes) {
        @NotNull References references = new ReferencesImpl();
        references.addAll(classes);

        return references;
    }

}
