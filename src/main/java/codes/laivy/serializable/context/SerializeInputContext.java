package codes.laivy.serializable.context;

import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.EOFException;
import java.lang.reflect.AnnotatedType;

public interface SerializeInputContext {

    @NotNull Class<?> getReference();

    /**
     * Reads a boolean value from the input context. This method reads the next
     * boolean field in the serialized data.
     *
     * @return The boolean value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    boolean readBoolean() throws EOFException;

    /**
     * Reads a byte value from the input context. This method reads the next
     * byte field in the serialized data.
     *
     * @return The byte value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    byte readByte() throws EOFException;

    /**
     * Reads a short value from the input context. This method reads the next
     * short field in the serialized data.
     *
     * @return The short value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    short readShort() throws EOFException;

    /**
     * Reads a char value from the input context. This method reads the next
     * char field in the serialized data.
     *
     * @return The char value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    char readChar() throws EOFException;

    /**
     * Reads an int value from the input context. This method reads the next
     * int field in the serialized data.
     *
     * @return The int value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    int readInt() throws EOFException;

    /**
     * Reads a long value from the input context. This method reads the next
     * long field in the serialized data.
     *
     * @return The long value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    long readLong() throws EOFException;

    /**
     * Reads a float value from the input context. This method reads the next
     * float field in the serialized data.
     *
     * @return The float value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    float readFloat() throws EOFException;

    /**
     * Reads a double value from the input context. This method reads the next
     * double field in the serialized data.
     *
     * @return The double value read from the context.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    double readDouble() throws EOFException;

    /**
     * Reads a line of text from the input context. This method reads the next
     * string value in the serialized data.
     * <p>
     * If the value is {@code null}, the method should handle this gracefully, either
     * by returning {@code null} or throwing an appropriate exception depending on
     * the context requirements.
     *
     * @return The string value read from the context, or {@code null} if the value is not available.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    @UnknownNullability String readLine() throws EOFException;

    /**
     * Reads a generic object from the input context. The object type is dynamically determined
     * based on the serialized data.
     * <p>
     * This method is flexible and can handle various types, including user-defined objects,
     * arrays, and other complex data structures.
     *
     * @param <E> The type of object being deserialized.
     * @param reference The reference of the object that will be read
     * @return The deserialized object, or {@code null} if the value is not available.
     * @throws EOFException If the end of the input stream is reached unexpectedly.
     */
    <E> @UnknownNullability E readObject(@NotNull Class<E> reference) throws EOFException;
    @UnknownNullability Object readObject(@NotNull Class<?> @NotNull [] references) throws EOFException;

    <E> @UnknownNullability E readField(@NotNull Class<E> reference, @NotNull String name);
    @NotNull String @NotNull [] getFields();

    @Nullable AnnotatedType getAnnotatedType();

    @NotNull JsonSerializer getSerializer();

}