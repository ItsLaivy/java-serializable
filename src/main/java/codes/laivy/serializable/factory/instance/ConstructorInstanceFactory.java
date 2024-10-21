package codes.laivy.serializable.factory.instance;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

final class ConstructorInstanceFactory implements InstanceFactory {

    /**
     * This class is responsible for creating an instance of an object using the default no-argument constructor.
     * <p>
     * The {@code ConstructorInstanceFactory} provides a mechanism for object instantiation through reflection by
     * invoking the class's no-argument constructor. This class ensures that the constructor is accessible,
     * bypassing any visibility restrictions (such as {@code private} constructors) by using reflection.
     * <p>
     * Before attempting to create an instance, this class checks that the provided class is concrete
     * (i.e., not abstract or an interface). If the class is abstract or an interface, or if a no-argument
     * constructor cannot be found, an {@code InstantiationException} is thrown.
     *
     * <p><b>Usage Notes:</b> This class should be used in scenarios where object creation through a no-argument
     * constructor is required, such as in dependency injection frameworks, testing environments, or serialization
     * tools where constructor-based instantiation is preferred.
     *
     * @param <E> the type of object to be instantiated.
     * @param reference the {@link Class} object representing the class to be instantiated; must not be {@code null}.
     * @return a new instance of the specified class, never {@code null}.
     * @throws InstantiationException if the class is abstract, an interface, or cannot be instantiated due to
     *                                the absence of a no-argument constructor or any other reflection-related issue.
     * @throws NullPointerException if {@code reference} is {@code null}.
     *
     * @see Constructor#newInstance(Object...)
     */
    @Override
    public <E> @NotNull E generate(@NotNull Class<E> reference) throws InstantiationException {
        // Modifiers check
        int mod = reference.getModifiers();
        if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
            throw new InstantiationException("the class must be concrete to be generated using allocator!");
        }

        // Try to generate using constructor
        try {
            // Generate instance
            @NotNull Constructor<?> constructor = reference.getDeclaredConstructor();
            constructor.setAccessible(true);

            //noinspection unchecked
            return (E) constructor.newInstance();
        } catch (@NotNull NoSuchMethodException e) {
            throw new InstantiationException("cannot find empty constructor at class '" + reference + "'");
        } catch (@NotNull InvocationTargetException | @NotNull InstantiationException | @NotNull IllegalAccessException e) {
            throw new InstantiationException("cannot generate empty constructor instance at class '" + reference + "': " + e.getMessage());
        }
    }

}
