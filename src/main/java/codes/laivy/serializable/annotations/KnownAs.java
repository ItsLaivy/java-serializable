package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * This annotation is used to explicitly specify the name that a field will be serialized as.
 * It allows you to customize the name used for a field during serialization.
 * <p>
 * Applying this annotation to a field will override the default serialization name
 * with the name provided in the annotation.
 * </p>
 * <p>
 * This annotation is retained at runtime and can be accessed through reflection.
 * </p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KnownAs {

    /**
     * Specifies the name that will be used for serializing the annotated field.
     * <p>
     * The provided name will replace the default field name in the serialized output.
     * </p>
     *
     * @return the name to use for serialization
     */
    @NotNull String name();

}