package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * This annotation allows for the exclusion of specific fields from serialization.
 * If the array is empty, all fields will be serialized normally (except transient fields).
 * <p>
 * When this annotation is applied, it generates serialized data without the specified fields.
 * This means that if an attempt is made to deserialize this data and the destination class does
 * not have this annotation on the field with the exact names as serialized, an exception will be thrown
 * due to the missing necessary fields.
 *
 * <p><strong>Note:</strong> The use of this annotation is not recommended, as it can disrupt the serialization
 * process and potentially lead to long-term code issues. Only use it if you are absolutely certain of what
 * you are doing.</p>
 *
 * <p><strong>Important:</strong> Excluded fields that are final and do not have defined values, when deserialized will be set to null.
 * Therefore, the user must be very careful when using this method and excluding final fields.
 * </p>
 *
 * Example usage:
 * <pre>
 * {@code
 * // Exclude the alpha from the Color class, we don't need alpha here!
 * @ExcludeFields(fields = {"falpha"})
 * private final UUID myField;
 * }
 * </pre>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcludeFields {

    /**
     * Specifies the names of the fields that should be excluded from serialization.
     * The names must be case-sensitive and must correspond to the actual field names in the class.
     *
     * @return An array of field names to be excluded from serialization. This array is guaranteed to be non-null.
     */
    @NotNull String @NotNull [] fields();
}