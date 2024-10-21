package codes.laivy.serializable.factory.instance;

import codes.laivy.serializable.Allocator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;

final class AllocatorInstanceFactory implements InstanceFactory {

    /**
     * This class is responsible for creating an instance of an object using the {@code Allocator},
     * which bypasses the constructor and skips field initialization.
     * <p>
     * The {@code AllocatorInstanceFactory} is typically used in situations where low-level control
     * over object instantiation is needed, such as in serialization frameworks or for performance optimizations
     * in cases where skipping object initialization is desirable.
     * <p>
     * The {@code generate} method ensures that the provided class is concrete (i.e., not abstract or an interface)
     * before attempting to create an instance. If the class is abstract or an interface, an {@code InstantiationException}
     * is thrown, as the {@code Allocator} can only instantiate concrete classes.
     *
     * @param <E> the type of object to be instantiated.
     * @param reference the {@link Class} object representing the class to be instantiated; must not be {@code null}.
     * @return a new instance of the specified class, never {@code null}.
     * @throws InstantiationException if the class is abstract, an interface, or otherwise cannot be instantiated.
     * @throws NullPointerException if {@code reference} is {@code null}.
     *
     * @see Allocator#allocate(Class)
     */
    @Override
    public <E> @NotNull E generate(@NotNull Class<E> reference) throws InstantiationException {
        // Modifiers check
        int mod = reference.getModifiers();
        if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
            throw new InstantiationException("the class must be concrete to be generated using allocator!");
        }

        // Finish
        return Allocator.allocate(reference);
    }

}
