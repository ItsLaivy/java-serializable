package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Indicates that the object of the annotated field should serialize certain transient fields as well.
 * The exact transient fields that need to be serialized can be configured through the {@link #fields()}
 * method, which should return the exact names (case-sensitive) of the transient fields. If the method
 * returns the name of a field that is not transient, it will still serialize normally without throwing an error.
 * <p>
 * If the array returned by the {@link #fields()} method is empty, all transient fields of the class will
 * be serialized along with the annotated field.
 *
 * <p><strong>Note:</strong> This is NOT RECOMMENDED! Serializing transient fields can severely
 * disrupt the functionality of the code. Only use this if you are absolutely sure of what you are doing.</p>
 *
 * Example usage:
 * <pre>
 * {@code
 * @IgnoreTransient(fields = {"temporaryData", "ignoredField"})
 * private final ClassWithTransients myField;
 * }
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreTransient {

    /**
     * Specifies the names of the transient fields that should be serialized along with the annotated field.
     * The names must be case-sensitive and correspond to the actual field names in the class. If an empty
     * array is returned, all transient fields will be serialized.
     *
     * @return An array of field names to be serialized. This array is guaranteed to be non-null.
     */
    @NotNull String @NotNull [] fields() default {};
}