package codes.laivy.serializable.annotations.serializers;

import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedSerializerException;
import codes.laivy.serializable.factory.context.MethodsContextFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * This annotation designates that specified serialization and deserialization methods
 * will be used to handle object serialization for the annotated class or field.
 * <p>
 * The serialization method must adhere to the following strict rules:
 * <ol>
 *     <li>It can have any access modifier (public, private, etc.).</li>
 *     <li>It must accept up to three parameters in the following sequence:
 *         <ul>
 *             <li>{@link Class} <strong>(Optional)</strong> (<span style="color:red">Never Null</span>): Represents the class reference of
 *             the object being serialized. It's useful in case the {@link Object} parameter is null</li>
 *         </ul>
 *         <ul>
 *             <li>{@link Object}: Any object type may be passed. However, if the object
 *             passed is incompatible with the method’s expected type, an exception will
 *             occur during serialization.</li>
 *         </ul>
 *         <ul>
 *             <li>{@link Config} <strong>(Optional)</strong> (<span style="color:red">Never Null</span>): Used to access configuration
 *             details related to the serialization process.</li>
 *         </ul>
 *     </li>
 *     <li>The return type can be any object (including null and excluding voids). The returned object will then be serialized
 *     recursively until the data is compatible with a valid {@link Context}.</li>
 *     <li>It must be static, so it’s not bound to an instance of the declaring class.</li>
 *     <li>The method can throw any exception, whether runtime or checked.</li>
 * </ol>
 * <p>
 * The deserialization method must adhere to these rules:
 * <ol>
 *     <li>It can have any access modifier.</li>
 *     <li>It must accept up to three parameters in the following sequence:
 *         <ul>
 *             <li>{@link Class} <strong>(Optional)</strong> (<span style="color:red">Never Null</span>): Represents the class reference of
 *             the object being deserialized.</li>
 *         </ul>
 *         <ul>
 *             <li>{@link Context} (or any concrete object) (<span style="color:red">Never Null</span>): Provides necessary information and utilities for reading
 *             the serialized data.</li>
 *         </ul>
 *         <ul>
 *             <li>{@link Config} <strong>(Optional)</strong> (<span style="color:red">Never Null</span>): Contains configuration data, allowing
 *             advanced deserialization behavior.</li>
 *         </ul>
 *     </li>
 *     <li>It must return any type of object. If the returned object does not match the
 *     expected type in the deserialization context, an exception will occur.</li>
 *     <li>The second parameter in deserialization methods may be any `Object`, and the serializer
 *     will automatically attempt to deserialize using the type of the provided reference object.
 *     To ensure accurate deserialization, only concrete objects should be specified here. Alternatively,
 *     the `@Concrete` annotation can be used to clearly define the concrete types for this parameter.</li>
 *     <li>The method can throw any exception.</li>
 * </ol>
 * <p>
 * If applied to a field but the field's type already has this annotation, the field-level annotation
 * will take precedence, providing more granular serialization control.
 * <p>
 * <strong>Important:</strong> If multiple valid methods for serialization or deserialization exist
 * (e.g., one with only {@link Object} and another with {@link Object} and {@link Config}),
 * the method with the most parameters will take precedence. For example, if both {@code #serialize(Object)}
 * and {@code #serialize(Object, Config)} are present, {@code #serialize(Object, Config)} will be chosen.
 *
 * <p><strong>Important:</strong> Ensure that serialized and deserialized types are compatible with
 * their respective methods to avoid exceptions or potential data corruption.</p>
 *
 * Example usages:
 * <pre>
 * {@code
 * @Serializers
 * public class CustomObject {
 *     // Fields and methods
 *
 *     public static @NotNull Context serialize(@NotNull CustomObject object) {
 *         // Serialization process here
 *         // return Context.from(object.getName());
 *     }
 *     public static @NotNull CustomObject deserialize(@NotNull Context context) throws EOFException {
 *         // Deserialization process here
 *         // return new CustomObject(context.getAsPrimitive().getAsString());
 *     }
 * }
 * }
 * </pre>
 * <pre>
 * {@code
 * @Serializers
 * public class CustomObject {
 *     // Fields and methods
 *
 *     public static @NotNull Object serialize(@NotNull CustomObject object, @NotNull Config config) {
 *         // Serialization process here
 *         // return config.getContext().serialize(object); // <- Uses the default serializer
 *     }
 *     public static @NotNull CustomObject deserialize(@NotNull Context context) throws EOFException {
 *         // Deserialization process here
 *         // return context.deserialize(); // <- Uses the default deserializer
 *     }
 * }
 * }
 * </pre>
 *
 * <p>If serializer/deserializer methods are missing, a {@link MalformedSerializerException} will be thrown.</p>
 * <p>If the serializer/deserializer cannot support the object, an {@link UnsupportedOperationException} will be thrown.</p>
 *
 * @see MethodsContextFactory this annotation uses this context factory
 * @since 1.1-SNAPSHOT
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface MethodSerialization {

    /**
     * Specifies the fully qualified name of the deserialization method to be used.
     * The method name must be provided in the format: [class name] "#" [method name].
     * If the class name is omitted, the method from the class where this annotation is applied
     * will be used.
     * <p>
     * Example valid values:
     * <ul>
     *     <li>"com.example.Serializer#customDeserialize" – explicit class and method name</li>
     *     <li>"com.example.Serializer$SubClass#customDeserialize" – explicit class and method in a nested class</li>
     *     <li>"#deserialize" – use the deserialization method from the same class where this annotation is present</li>
     * </ul>
     *
     * @return The deserialization method name in the required format.
     */
    @NotNull String deserialization() default "#deserialize";

    /**
     * Specifies the fully qualified name of the serialization method to be used.
     * The format follows the same rules as {@link #deserialization()}, including the
     * option to omit the class name if the method belongs to the current class.
     * <p>
     * Example valid values:
     * <ul>
     *     <li>"com.example.Serializer#customSerialize" – explicit class and method name</li>
     *     <li>"com.example.Serializer$SubClass#customSerialize" – explicit class and method in a nested class</li>
     *     <li>"#serialize" – use the serialization method from the same class where this annotation is present</li>
     * </ul>
     *
     * @return The serialization method name in the required format.
     */
    @NotNull String serialization() default "#serialize";
}