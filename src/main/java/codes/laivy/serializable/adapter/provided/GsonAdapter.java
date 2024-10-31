package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.*;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.Objects;
import java.util.function.Function;

public final class GsonAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] {
                JsonObject.class,
                JsonPrimitive.class,
                JsonArray.class,
                JsonNull.class
        };
    }

    @Override
    public @NotNull Context write(@NotNull Class<?> reference, @Nullable Object element, @NotNull Serializer serializer, @NotNull Config config) {
        if (element == null) {
            return NullContext.create();
        }

        if (element instanceof JsonObject) {
            @NotNull MapContext context = MapContext.create(serializer);
            @NotNull JsonObject object = (JsonObject) element;

            for (@NotNull String key : object.keySet()) {
                @NotNull JsonElement value = object.get(key);
                context.setContext(key, (Context) Objects.requireNonNull(write(value.getClass(), value, serializer, Config.builder().build())));
            }

            return context;
        } else if (element instanceof JsonArray) {
            @NotNull ArrayContext context = ArrayContext.create(serializer);
            @NotNull JsonArray array = (JsonArray) element;

            for (@NotNull JsonElement target : array) {
                context.write((Context) Objects.requireNonNull(write(target.getClass(), target, serializer, Config.builder().build())));
            }

            return context;
        } else if (element instanceof JsonPrimitive) {
            @NotNull JsonPrimitive primitive = (JsonPrimitive) element;

            if (primitive.isBoolean()) {
                return PrimitiveContext.create(primitive.getAsBoolean());
            } else if (primitive.isString()) {
                return PrimitiveContext.create(primitive.getAsString());
            } else if (primitive.isNumber()) {
                return PrimitiveContext.create(primitive.getAsNumber());
            } else {
                throw new UnsupportedOperationException("cannot parse primitive json type '" + element + "'");
            }
        } else if (element instanceof JsonNull) {
            return NullContext.create();
        } else {
            throw new UnsupportedOperationException("this reference '" + element.getClass().getName() + "' isn't supported by gson adapter");
        }
    }

    @Override
    public @NotNull Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws EOFException {
        // Functions
        @NotNull Function<Context, Class<? extends JsonElement>> locator = new Function<Context, Class<? extends JsonElement>>() {
            @Override
            public @NotNull Class<? extends JsonElement> apply(@NotNull Context context) {
                if (context.isMap()) return JsonObject.class;
                else if (context.isArray()) return JsonArray.class;
                else if (context.isPrimitive()) return JsonPrimitive.class;
                else return JsonNull.class;
            }
        };

        // Deserialize
        if (reference == JsonObject.class) {
            if (!context.isMap()) {
                throw new IllegalStateException("to deserialize a JsonObject the context should be a Map Context!");
            }

            @NotNull MapContext map = (MapContext) context;
            @NotNull JsonObject object = new JsonObject();

            for (@NotNull String key : map.keySet()) {
                @NotNull Context value = map.getContext(key);
                @NotNull Class<?> valueClass = locator.apply(value);

                object.add(key, (JsonElement) read(valueClass, serializer, context, Config.builder(serializer, valueClass).build()));
            }

            return object;
        } else if (reference == JsonArray.class) {
            if (!context.isArray()) {
                throw new IllegalStateException("to deserialize a JsonArray the context should be an Array Context!");
            }

            @NotNull JsonArray array = new JsonArray();

            for (@NotNull Context value : (ArrayContext) context) {
                @NotNull Class<?> valueClass = locator.apply(value);
                array.add((JsonElement) read(valueClass, serializer, context, Config.builder(serializer, valueClass).build()));
            }

            return array;
        } else if (reference == JsonPrimitive.class) {
            if (!context.isPrimitive()) {
                throw new IllegalStateException("to deserialize a JsonPrimitive the context should be a Primitive Context!");
            }

            @NotNull PrimitiveContext primitive = (PrimitiveContext) context;

            if (primitive.isBoolean()) {
                return new JsonPrimitive(primitive.getAsBoolean());
            } else if (primitive.isNumber()) {
                return new JsonPrimitive(primitive.getAsNumber());
            } else if (primitive.isString()) {
                return new JsonPrimitive(primitive.getAsString());
            } else {
                throw new IllegalStateException("illegal primitive value '" + primitive + "'");
            }
        } else {
            return JsonNull.INSTANCE;
        }
    }
}
