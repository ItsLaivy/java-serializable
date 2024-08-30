package codes.laivy.serializable.json;

import codes.laivy.serializable.adapter.Adapter;
import com.google.gson.JsonElement;

public interface JsonAdapter<O> extends Adapter<JsonElement, O> {
}
