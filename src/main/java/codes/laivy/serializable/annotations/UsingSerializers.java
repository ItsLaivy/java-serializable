package codes.laivy.serializable.annotations;

import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.exception.MalformedSerializerException;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * This annotation designates that the specified serialization and deserialization methods
 * will be used to handle object serialization for the annotated class or field.
 * <p>
 * The serialization method must adhere to the following strict rules:
 * <ol>
 *     <li>It can have any access modifier (public, private, etc.).</li>
 *     <li>It must accepts only one parameter:
 *         <ul>
 *             <li>{@link Object}: This can be any type of object. However, if the object passed during
 *             serialization is not compatible with the type expected by the method, an exception will
 *             be thrown during deserialization.</li>
 *         </ul>
 *         <ul>
 *             <li>{@link SerializationProperties} <strong>(Optional)</strong>: This parameter is optional, and is used to retrieve some
 *             essential configurations about the serialization process properties</li>
 *         </ul>
 *     </li>
 *     <li>It must return {@link Context}, with the serialized data inside.</li>
 *     <li>It must be static, meaning it is not bound to a particular instance of the class where it is declared.</li>
 *     <li>The method can throw anything (Runtime or not exceptions).</li>
 * </ol>
 * <p>
 * The deserialization method must follow these specific rules:
 * <ol>
 *     <li>It can have any access modifier.</li>
 *     <li>It must accepts only one parameter:
 *         <ul>
 *             <li>{@link Context}: The context that contains the necessary information and
 *             utilities for reading the serialized data.</li>
 *         </ul>
 *     </li>
 *     <li>It must return an object of any type. However, if the returned object is not compatible with
 *     the object expected by the deserialization context, an exception will be thrown during deserialization.</li>
 *     <li>The method can throw anything (Runtime or not exceptions).</li>
 * </ol>
 * <p>
 * If this annotation is applied to a field, but the class type of the field already has this annotation,
 * the field-level annotation will take precedence, allowing more granular control over serialization
 * behavior at the field level.
 *
 * <p><strong>Important:</strong> It is crucial to ensure that the serialized and deserialized object types are
 * compatible with the serialization and deserialization methods, as improper configurations can lead
 * to exceptions and potential data corruption during the deserialization process.</p>
 *
 * Example usages:
 * <pre>
 * {@code
 * @UsingSerializers
 * public class CustomObject {
 *     // Fields and methods
 *
 *     public static @NotNull Context serialize(@NotNull CustomObject object) {
 *         // Serialization process here
 *         // return context.serialize(object); // <- The default serializer
 *     }
 *     public static @NotNull CustomObject deserialize(@NotNull Context context) throws EOFException {
 *         // Deserialization process here
 *         // return context.deserialize(); // <- The default deserializer
 *     }
 *
 * }
 * }
 * </pre>
 * <pre>
 * {@code
 * @UsingSerializers
 * public class CustomObject {
 *     // Fields and methods
 *
 *     public static @NotNull Context serialize(@NotNull CustomObject object, @Nullable SerializationProperties properties) {
 *         // Serialization process here
 *         // return context.serialize(object); // <- The default serializer
 *     }
 *     public static @NotNull CustomObject deserialize(@NotNull Context context) throws EOFException {
 *         // Deserialization process here
 *         // return context.deserialize(); // <- The default deserializer
 *     }
 *
 * }
 * }
 * </pre>
 *
 * <p>If the serializer/deserializer methods is missing, an {@link MalformedSerializerException} exception will be thrown</p>
 * <p>If the serializer/deserializer doesn't supports the object (the parameter of the serialize or the return of deserialize not assignable), an {@link UnsupportedOperationException} exception will be thrown</p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
// todo: serialization can return any type
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface UsingSerializers {

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