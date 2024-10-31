package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.ArrayContext;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.MapContext;
import codes.laivy.serializable.exception.IllegalConcreteTypeException;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.script.SimpleBindings;
import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

// todo: CharSequence
public final class MapAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] {
                ConcurrentSkipListMap.class,
                HashMap.class,
                Hashtable.class,
                IdentityHashMap.class,
                LinkedHashMap.class,
                PrinterStateReason.class,
                Properties.class,
                SimpleBindings.class,
                TreeMap.class,
                UIDefaults.class,
                WeakHashMap.class
        };
    }

    @Override
    public @Nullable Object write(@NotNull Class<?> reference, @Nullable Object element, @NotNull Serializer serializer, @NotNull Config config) {
        if (element == null) {
            return null;
        } else if (element instanceof Map) {
            @NotNull Map<?, ?> instance = (Map<?, ?>) element;
            @NotNull Class<?> keyReference = config.getGenerics().stream().findFirst().orElseThrow(UnsupportedOperationException::new);

            if (keyReference == String.class) {
                @NotNull MapContext map = MapContext.create(serializer);

                for (@NotNull Entry<?, ?> entry : instance.entrySet()) {
                    @NotNull String key = (String) entry.getKey();
                    @NotNull Object value = entry.getValue();

                    map.setObject(key, value);
                }

                return map;
            } else {
                @NotNull ArrayContext array = ArrayContext.create(serializer);

                for (@NotNull Entry<?, ?> entry : instance.entrySet()) {
                    @NotNull MapContext context = MapContext.create(serializer);

                    context.setObject("key", entry.getKey());
                    context.setObject("value", entry.getValue());

                    array.add(context);
                }

                return array;
            }
        } else {
            throw new UnsupportedOperationException("cannot write reference '" + reference.getName() + "' using map adapter");
        }
    }
    @SuppressWarnings({"ExtractMethodRecommender", "unchecked", "rawtypes"})
    @Override
    public @Nullable Object read(@NotNull Class<?> reference, @NotNull Serializer serializer, @NotNull Context context, @NotNull Config config) throws IOException, InstantiationException {
        if (context.isNull()) {
            return null;
        } else {
            // Generate instance
            @NotNull Map object;

            if (reference == ConcurrentSkipListMap.class) {
                object = new ConcurrentSkipListMap<>();
            } else if (reference == HashMap.class) {
                object = new HashMap<>();
            } else if (reference == Hashtable.class) {
                object = new Hashtable<>();
            } else if (reference == IdentityHashMap.class) {
                object = new IdentityHashMap<>();
            } else if (reference == LinkedHashMap.class) {
                object = new LinkedHashMap<>();
            } else if (reference == PrinterStateReason.class) {
                object = new PrinterStateReasons();
            } else if (reference == Properties.class) {
                object = new Properties();
            } else if (reference == SimpleBindings.class) {
                object = new SimpleBindings();
            } else if (reference == TreeMap.class) {
                object = new TreeMap<>();
            } else if (reference == UIDefaults.class) {
                object = new UIDefaults();
            } else if (reference == WeakHashMap.class) {
                object = new WeakHashMap<>();
            } else {
                throw new UnsupportedOperationException("you cannot use the reference '" + reference.getName() + "' at the map adapter");
            }

            // Reading

            if (context.isMap()) {
                @NotNull MapContext map = context.getAsMap();

                @NotNull Class<?> keyReference = config.getGenerics().stream().findFirst().orElseThrow(UnsupportedOperationException::new);
                if (keyReference != String.class) {
                    throw new UnsupportedOperationException("to deserialize a map using the MapContext at the map adapter, the key must be a string!");
                }

                // Fill map
                for (@NotNull Entry<@NotNull String, @NotNull Context> entry : map.entrySet()) {
                    @NotNull String key = entry.getKey();
                    @NotNull Context sub = entry.getValue();

                    object.put(key, sub);
                }

                // Finish
                return object;
            } else if (context.isArray()) {
                @NotNull ArrayContext array = context.getAsArray();

                // Retrieve information and fill map
                for (@NotNull Context temp : array) {
                    @NotNull MapContext map = temp.getAsMap();

                    @NotNull Class<?> @NotNull [] keys = config.getGenerics(object.getClass().getGenericInterfaces()[0]).toArray(new Class[0]);
                    @NotNull Class<?> @NotNull [] values = config.getGenerics(object.getClass().getGenericInterfaces()[1]).toArray(new Class[0]);

                    @Nullable Object key;
                    @Nullable Object value;

                    keyBlock:
                    {
                        for (@NotNull Class<?> keyReference : keys) try {
                            key = map.getObject(keyReference, "key");
                            break keyBlock;
                        } catch (@NotNull IncompatibleReferenceException | @NotNull IllegalConcreteTypeException ignore) {
                        }

                        throw new IncompatibleReferenceException("there's no compatible references '" + Arrays.toString(keys) + "' to deserialize map key element: " + map.getContext("key"));
                    }

                    valueBlock:
                    {
                        for (@NotNull Class<?> valueReference : values) try {
                            value = map.getObject(valueReference, "value");
                            break valueBlock;
                        } catch (@NotNull IncompatibleReferenceException | @NotNull IllegalConcreteTypeException ignore) {
                        }

                        throw new IncompatibleReferenceException("there's no compatible references '" + Arrays.toString(values) + "' to deserialize map value element: " + map.getContext("value"));
                    }

                    object.put(key, value);
                }

                // Finish
                return object;
            } else {
                throw new UnsupportedOperationException("the map adapter only supports array contexts");
            }
        }
    }

}
