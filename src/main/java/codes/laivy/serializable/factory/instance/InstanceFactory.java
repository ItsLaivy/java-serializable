package codes.laivy.serializable.factory.instance;

import codes.laivy.serializable.Allocator;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code InstanceFactory} interface provides a strategy for creating instances of objects.
 * <p>
 * This interface defines methods to generate objects through two primary methods: using an {@code Allocator},
 * which bypasses constructor invocation, or by invoking a no-argument constructor. These strategies are useful in
 * scenarios where different approaches to object instantiation are required, such as in frameworks that manage
 * object lifecycle, dependency injection, or serialization.
 *
 * @see AllocatorInstanceFactory
 * @see ConstructorInstanceFactory
 */
public interface InstanceFactory {

    // Static initializers

    /**
     * Creates an {@link InstanceFactory} that generates objects without invoking their constructors.
     * <p>
     * This method uses the {@link Allocator} to directly allocate memory for a new object without calling its
     * constructor or initializing its fields. This approach is commonly used in low-level libraries, serialization
     * frameworks, or performance-critical scenarios where standard object initialization needs to be bypassed.
     *
     * @return an {@link InstanceFactory} that allocates objects without invoking constructors.
     * @see AllocatorInstanceFactory
     */
    static @NotNull InstanceFactory allocator() {
        return new AllocatorInstanceFactory();
    }

    /**
     * Creates an {@link InstanceFactory} that generates objects using a no-argument constructor.
     * <p>
     * This method requires the target class to have an explicitly declared no-argument constructor. It invokes
     * the constructor to generate a new instance, providing more control over when and how the class is initialized.
     * This is useful in scenarios where the constructor logic must be executed, such as in dependency injection frameworks
     * or situations where final fields cannot be bypassed.
     *
     * @return an {@link InstanceFactory} that creates objects by invoking a no-argument constructor.
     * @see ConstructorInstanceFactory
     */
    static @NotNull InstanceFactory constructor() {
        return new ConstructorInstanceFactory();
    }

    // Object

    /**
     * Generates a new instance of the specified class.
     * <p>
     * This method creates an instance of the provided class {@code reference}. Depending on the implementation of
     * {@code InstanceFactory}, the instance may be created using different strategies, such as allocation without
     * constructor invocation or standard instantiation using a constructor. The target class must be concrete,
     * meaning it cannot be abstract or an interface.
     *
     * @param <E> the type of object to be instantiated.
     * @param reference the {@link Class} object representing the class to be instantiated; must not be {@code null}.
     * @return a new instance of the specified class, never {@code null}.
     * @throws InstantiationException if the class is abstract, an interface, or cannot be instantiated for any other reason.
     * @throws NullPointerException if {@code reference} is {@code null}.
     */
    <E> @NotNull E generate(@NotNull Class<E> reference) throws InstantiationException;
}