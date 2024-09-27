package codes.laivy.serializable.annotations;

import java.lang.annotation.*;

/**
 * If a class uses this annotation, it explicitly indicates that the serializer
 * must construct an instance of the class using the empty constructor.
 * <p>
 * The class must have a default (empty) constructor available; otherwise, an exception
 * will be thrown during the serialization process.
 *
 * <p><strong>Note:</strong> It is essential to ensure that a no-argument constructor exists and is accessible
 * when applying this annotation, as failure to do so will result in a runtime error during the creation
 * of the instance.</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseEmptyConstructor {
}
