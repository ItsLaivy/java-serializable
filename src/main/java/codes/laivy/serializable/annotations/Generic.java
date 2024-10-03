package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Indicates a generic type for the serialization and deserialization process.
 * This annotation can be applied to type uses to specify the concrete type
 * that should be used during serialization and deserialization, enabling
 * a cleaner and clearer representation of the generic types.
 *
 * <p>This annotation is particularly useful for adapters within the library,
 * as it provides the necessary type information to customize the serialization
 * and deserialization behavior. By utilizing this annotation, developers can
 * ensure that the correct types are recognized by the serialization framework,
 * thereby improving the integrity and reliability of the data being processed.</p>
 *
 * <p>Note: The class specified in the {@code type()} method must be a concrete
 * class. If a non-concrete class (such as an interface or abstract class)
 * is provided, a runtime error will be thrown during serialization or
 * deserialization.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     {@code
 * static final @NotNull Map<@Generic(type = String.class) String,
 *                           @Generic(type = Integer.class) List<Integer>> map =
 *                           new HashMap<>();
 *     }
 * </pre>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Concretes.Generics.class)
@Target({ElementType.TYPE_USE})
public @interface Generic {
    /**
     * Specifies the concrete class type associated with the generic type.
     *
     * <p>Note: The class provided must be a concrete class. Providing a
     * non-concrete class (e.g., an interface or abstract class) will result
     * in a runtime error.</p>
     *
     * @return the class object representing the concrete type.
     */
    @NotNull Class<?> type();
}
