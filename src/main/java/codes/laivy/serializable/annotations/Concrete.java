package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation used to specify the concrete class that should be used by the serializer when it cannot infer
 * the exact type of the field. This situation arises when the field's reference type is either an interface or an
 * abstract class, making it unclear which concrete implementation to use. By applying this annotation to such fields,
 * the developer explicitly indicates which concrete class the serializer should use.
 *
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
 * possible concrete types. O serializador tentará desserializar o campo usando o primeiro tipo concreto válido.
 * <p>
 * Note: Using multiple @Concrete annotations can be problematic. The serializer will use the first concrete class
 * that is compatible with the serialized value. As example, if there are two concrete values for a class, Dog and Cat,
 * but the serializable value represents a Cat that is also compatible with Dog, the result could incorrectly be a Dog.
 * </p>
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
 * // The address can have variations, so we include all of them into multiple @Concrete annotations
 * @Concrete(type = IPv4Address.class)
 * @Concrete(type = IPv6Address.class)
 * private final Address address;
 * }
 * </pre>
 *
 * <p>
 * This annotation now supports type use, allowing it to be applied in generic type definitions. For example:
 * <pre>
 * {@code
 * // Indicates that the element Animal in the list can be either Cat or Dog.
 * @Concrete(type = ArrayList.class)
 * List<@Concrete(type = Cat.class) @Concrete(type = Dog.class) Animal> list = new ArrayList<>();
 * }
 * </pre>
 * In cases where non-concrete types are defined, it is MANDATORY to have at least one @Concrete annotation.
 * </p>
 *
 * <p>
 * Note: In some cases, annotations may be lost at runtime. This is evident in scenarios involving certain collection types
 * (e.g., KeySetView from ConcurrentHashMap), where the annotations on generic parameters may not be accessible due
 * to type erasure and the internal structure of these classes. This means that even if annotations are present in the
 * source code, they might not be retrievable through reflection during runtime, particularly for internal or
 * complex structures.
 * </p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Concretes.class)
@Target({ElementType.TYPE_USE, ElementType.FIELD})
public @interface Concrete {

    /**
     * Specifies the concrete class to be used by the serializer for the annotated field.
     * The specified class must be a concrete class and should be assignable to the field's declared type.
     * This class will be used during deserialization to instantiate the correct type, but it no longer
     * overrides any pre-existing value in the field.
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