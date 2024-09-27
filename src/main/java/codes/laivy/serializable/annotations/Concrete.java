package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation used to specify the concrete class that should be used by the serializer when it cannot infer
 * the exact type of a field. This situation arises when the field's reference type is either an interface or an
 * abstract class, making it unclear which concrete implementation to use. By applying this annotation to such fields,
 * the developer explicitly indicates which concrete class the serializer should use.
 * <p>
 * The class specified in the annotation's parameter MUST be a concrete class and should adhere to the following constraints:
 * <ul>
 *     <li>It must not be an abstract class or an interface.</li>
 *     <li>It must be a subclass (or implementor) of the field's declared type.</li>
 * </ul>
 * <p>
 * This is particularly useful in cases where polymorphism is in use, and the serializer cannot determine the appropriate
 * concrete type for deserialization.
 * <p>
 * This annotation can also be applied multiple times to the same field, allowing the developer to specify multiple
 * possible concrete types. The serializer will attempt to deserialize the field using the first valid concrete type.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * // Using multiple concrete types for deserialization
 * @Concrete(type = ArrayList.class)
 * @Concrete(type = LinkedList.class)
 * private final List<String> items;
 * }
 * </pre>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Concretes.class)
@Target(ElementType.FIELD)
public @interface Concrete {

    /**
     * Specifies the concrete class to be used by the serializer for the annotated field.
     * The specified class must be a concrete class and should be assignable to the field's declared type.
     * This class will be used during deserialization to instantiate the correct type.
     * <p>
     * Constraints:
     * <ul>
     *     <li>The class must be concrete (i.e., it must not be abstract or an interface).</li>
     *     <li>It must be compatible with the field's declared type (i.e., a subclass or implementor).</li>
     * </ul>
     *
     * @return The concrete class to be used for the annotated field. The returned class is guaranteed to be non-null.
     */
    @NotNull Class<?> type();
}