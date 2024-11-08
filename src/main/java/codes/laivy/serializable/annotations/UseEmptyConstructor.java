package codes.laivy.serializable.annotations;

import codes.laivy.serializable.annotations.serializers.Serializers;

import java.lang.annotation.*;

/**
 * If a class uses this annotation, it explicitly indicates that the serializer
 * must construct an instance of the class using the empty constructor.
 * <p>
 * The class must have a default (empty) constructor available; otherwise, an exception
 * will be thrown during the serialization process.
 *
 * <p><strong>Note:</strong> It is essential to ensure that a no-argument constructor exists
 * when applying this annotation, as failure to do so will result in a runtime error during the creation
 * of the instance.</p>
 *
 * <p><strong>Note ²:</strong> If the field/class uses {@link Serializers} annotation, it's not guaranteed the
 * usage of this annotation, since the custom serializers generally instances the objects using the available constructors</p>
 * or any other way, ignoring the native allocators</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface UseEmptyConstructor {
}
