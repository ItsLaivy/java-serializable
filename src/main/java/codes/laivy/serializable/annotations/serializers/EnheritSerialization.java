package codes.laivy.serializable.annotations.serializers;

import java.lang.annotation.*;

/**
 * This annotation indicates that the serialization mechanism defined in the superclass
 * should be used recursively for the annotated class.
 * <p>
 * If a class, such as `A`, is annotated with this and extends `B`, while `B` is also annotated and extends `C`,
 * the serializer from `C` will be used, provided it is compatible with `A`.
 * <p>
 * Important considerations when using this annotation:
 * <ul>
 *     <li>The superclass containing the serializer must be compatible with all subclasses that inherit it.</li>
 *     <li>The serialization method must meet the conditions and structure expected by the inheriting class.</li>
 *     <li>The superclass with the expected serializer class must include the {@link MethodSerialization} annotation. If not, an exception will be thrown during
 *     the serialization process.</li>
 * </ul>
 * <p>
 * The purpose of this annotation is to ensure a consistent serialization pattern across
 * a hierarchy of classes by utilizing a common serializer implementation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnheritSerialization {
}