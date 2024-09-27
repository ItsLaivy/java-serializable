package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation used to specify the concrete class that should be used by the serializer when it cannot infer
 * the exact type of field. This situation arises when the field's reference type is either an interface or an
 * abstract class, making it unclear which concrete implementation to use. By applying this annotation to such fields,
 * the developer explicitly indicates which concrete class the serializer should use.
 * <p>
 * If the field already has a defined value, the class of that existing value will be used as the concrete class,
 * unless there is a {@code @Concrete} annotation present. In that case, the first {@code @Concrete} annotation
 * will take priority over the existing value's class if compatible.
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
 * // Specifies the serializer to use ArrayList
 * @Concrete(type = ArrayList.class)
 * private final List<String> items;
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * // The @Concrete annotation is not needed if the field already has a defined value,
 * // such as a LinkedList, which will be used as the concrete class.
 * private final List<String> items = new LinkedList<>();
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * // The address can have variations, so we include all of them into multiples @Concrete annotations
 * @Concrete(type = IPv4Address.class)
 * @Concrete(type = IPv6Address.class)
 * private final Address address;
 *
 * // Try out my java-address library, available on my (@itslaivy) github :)
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
     * This class will be used during deserialization to instantiate the correct type, unless the field already
     * has a defined value, in which case the existing value's class will be used unless {@code @Concrete} is present.
     * In such a case, the first {@code @Concrete} annotation will take precedence.
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