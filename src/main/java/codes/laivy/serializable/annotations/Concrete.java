package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation used to specify one or more concrete classes that should be used by the serializer when it cannot infer
 * the exact type of a field, class, or generic parameter. This is useful in scenarios where a field’s declared type
 * is abstract (such as an interface or abstract class), making it unclear which concrete implementation to use.
 *
 * <p>
 * `@Concrete` can be applied at multiple levels:
 * <ul>
 *     <li><b>Class level:</b> Allows marking an abstract class with valid concrete implementations, which are used
 *         by the serializer during deserialization.</li>
 *     <li><b>Field level:</b> Defines specific concrete implementations for abstract fields, guiding the serializer
 *         on which type to instantiate if the field is abstract.</li>
 *     <li><b>Generic parameter level:</b> In generic types, multiple `@Concrete` annotations can be applied to
 *         define various compatible concrete implementations for a single generic parameter, enhancing flexibility
 *         in collections or other generic structures. Example:
 *         <pre>
 *         {@code
 *         List<@Concrete(type = Dog.class) @Concrete(type = Cat.class) Animal> animals = new ArrayList<>();
 *         }
 *         </pre>
 * </ul>
 * </p>
 *
 * <p>
 * The specified class in each `@Concrete` annotation must meet the following constraints:
 * <ul>
 *     <li>It must be a concrete class (i.e., it cannot be abstract or an interface).</li>
 *     <li>It must be compatible with the declared type of the field, class, or generic parameter.</li>
 * </ul>
 * </p>
 *
 * <p>
 * When multiple `@Concrete` annotations are applied, the serializer will use the first compatible class found.
 * However, this flexibility requires careful management to avoid unintended deserialization results.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * // Specifies ArrayList as the serializer's concrete implementation
 * @Concrete(type = ArrayList.class)
 * private final List<String> items;
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * // Allows multiple address implementations
 * @Concrete(type = IPv4Address.class)
 * @Concrete(type = IPv6Address.class)
 * private final Address address;
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * // Apply to a class to indicate valid concrete implementations
 * @Concrete(type = Cat.class)
 * @Concrete(type = Dog.class)
 * public abstract class Animal {
 *     // Fields and methods
 * }
 * }
 * </pre>
 *
 * <p>
 * Limitations: Annotations may be lost in some cases due to type erasure or internal collection structures, especially
 * in scenarios involving complex generic types.
 * </p>
 *
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Concretes.class)
@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.TYPE})
public @interface Concrete {

    /**
     * Specifies the concrete class to be used by the serializer for the annotated element. The class must be concrete
     * and compatible with the declared type.
     *
     * @return The concrete class for serialization. Guaranteed to be non-null.
     */
    @NotNull Class<?> type();
}