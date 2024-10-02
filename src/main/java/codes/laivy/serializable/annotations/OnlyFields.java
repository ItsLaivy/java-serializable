package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * This annotation allows only specific fields to be serialized. Similar to the
 * {@link ExcludeFields} annotation, the key difference is that this annotation is
 * inclusive, meaning only the chosen fields will be serialized.
 * <p>
 * When this annotation is applied, it results in serialized data containing only the
 * specified fields. If any of the specified fields are not present in the class during
 * deserialization, an exception will be thrown due to the absence of required fields.
 *
 * <p><strong>Important:</strong> Excluded fields must have a default value if they are final;
 * otherwise, they will be set to null during deserialization, which may lead to unintended behavior.
 * Fields that are not final do not have this restriction.</p>
 *
 * <p><strong>Note:</strong> Using this annotation can be risky. It is important to understand the
 * implications of limiting serialization to specific fields, as it can lead to data loss or
 * serialization issues. Only use it if you are absolutely sure of what you are doing.</p>
 *
 * If applied with a {@link ExcludeFields} annotation, this annotation will get more priority and all the
 * attributes from the {@link ExcludeFields} will be ignored.
 *
 * Example usage:
 * <pre>
 * {@code
 * // We just want the color attributes, not the alpha xD
 * @OnlyFields(fields = {"value", "frgbvalue", "fvalue"})
 * private final Color colorWithoutAlpha;
 * }
 * </pre>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OnlyFields {

    /**
     * Specifies the names of the fields that should be included in the serialization.
     * The names must be case-sensitive and must correspond to the actual field names in the class.
     *
     * @return An array of field names to be included in the serialization. This array is guaranteed to be non-null.
     */
    @NotNull String @NotNull [] fields();
}