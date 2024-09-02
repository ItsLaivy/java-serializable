package codes.laivy.serializable.json.adapter;

import codes.laivy.serializable.adapter.Adapter;
import com.google.gson.JsonElement;

/**
 * The `JsonAdapter` interface extends the {@link Adapter} interface, specializing it for
 * serializing and deserializing objects of type `O` to and from JSON representation using {@link JsonElement}.
 *
 * <p>This interface allows for the implementation of custom JSON serialization strategies for
 * various object types. By extending the `Adapter` interface, `JsonAdapter` leverages the
 * general serialization and deserialization contract while specifying `JsonElement` as the
 * serialized form.</p>
 *
 * @param <O> The type of object to be serialized to and deserialized from a {@link JsonElement}.
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.1-SNAPSHOT
 */
public interface JsonAdapter<O> extends Adapter<JsonElement, O> {
}
