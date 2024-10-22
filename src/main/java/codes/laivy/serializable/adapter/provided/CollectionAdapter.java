package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.annotations.Concretes;
import codes.laivy.serializable.context.ArrayContext;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.properties.SerializationProperties;
import codes.laivy.serializable.reference.References;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.lang.reflect.AnnotatedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static codes.laivy.serializable.utilities.Classes.isConcrete;

public class CollectionAdapter implements Adapter {

    public static final @NotNull Class<?> ARRAYS_ARRAYLIST;

    static {
        try {
            ARRAYS_ARRAYLIST = Class.forName("java.util.Arrays$ArrayList");
        } catch (@NotNull ClassNotFoundException e) {
            throw new RuntimeException("cannot retrieve Arrays' ArrayList inner class", e);
        }
    }

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] {
                ARRAYS_ARRAYLIST,
                ArrayList.class,
                LinkedList.class,
                Vector.class,
                Stack.class,
                CopyOnWriteArrayList.class,

                KeySetView.class,
                ConcurrentSkipListSet.class,
                CopyOnWriteArraySet.class,
                HashSet.class,
                LinkedHashSet.class,
                TreeSet.class
        };
    }

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        if (object instanceof Collection<?>) {
            // Start
            @NotNull Collection<?> collection = (Collection<?>) object;
            @NotNull ArrayContext context = ArrayContext.create(serializer);

            // Add elements to collection
            for (@Nullable Object element : collection) {
                context.write(element);
            }

            // Finish
            return context;
        } else {
            throw new UnsupportedOperationException("this adapter only supports some collection types!");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public @NotNull Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException {
        // Initialize variables
        @NotNull ArrayContext array = context.getAsArrayContext();
        @NotNull Collection<Class<?>> temp = new LinkedHashSet<>();
        @NotNull Collection collection;

        // Retrieve generic types
        if (context.getProperties() == null) {
            throw new UnsupportedOperationException("a collection requires a valid SerializingProperties data with the generic concrete elements");
        } else {
            @NotNull SerializationProperties properties = context.getProperties();
            // Function to help add concrete types
            @NotNull Consumer<AnnotatedType> addConcreteTypes = (t) -> {
                if (t.getType() instanceof Class && isConcrete((Class<?>) t.getType())) {
                    temp.add((Class<?>) t.getType());
                }

                temp.addAll(Arrays.stream(t.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toList()));
                temp.addAll(Arrays.stream(t.getAnnotationsByType(Concretes.class)).flatMap(concretes -> Arrays.stream(concretes.value())).map(Concrete::type).collect(Collectors.toList()));
            };

            // Add generic concrete references
            temp.addAll(properties.getGenericConcretes(reference.getGenericInterfaces()[0]));
        }

        // Functions
        @NotNull References references = References.of(temp);

        @NotNull Consumer<Collection<Object>> adder = objects -> {
            while (true) {
                try {
                    @Nullable Object object = array.readObject(references);
                    objects.add(object);
                } catch (@NotNull EOFException ignore) {
                    break;
                } catch (@NotNull Throwable throwable) {
                    throw new RuntimeException("cannot deserialize object to collection '" + objects.getClass().getName() + "' using references " + references, throwable);
                }
            }
        };

        // Read
        if (reference == ARRAYS_ARRAYLIST) {
            @NotNull List<@Nullable Object> list = new LinkedList<>();
            adder.accept(list);

            return Arrays.asList(list.toArray());
        } else {
            if (reference == ArrayList.class) {
                collection = new ArrayList<>();
            } else if (reference == LinkedList.class) {
                collection = new LinkedList<>();
            } else if (reference == Stack.class) {
                collection = new Stack<>();
            } else if (reference == CopyOnWriteArrayList.class) {
                collection = new CopyOnWriteArrayList<>();
            } else if (reference == KeySetView.class) {
                collection = ConcurrentHashMap.newKeySet();
            } else if (reference == ConcurrentSkipListSet.class) {
                collection = new ConcurrentSkipListSet();
            } else if (reference == CopyOnWriteArraySet.class) {
                collection = new CopyOnWriteArraySet();
            } else if (reference == HashSet.class) {
                collection = new HashSet();
            } else if (reference == LinkedHashSet.class) {
                collection = new LinkedHashSet();
            } else if (reference == TreeSet.class) {
                collection = new TreeSet();
            } else if (reference == Vector.class) {
                collection = new Vector();
            } else {
                throw new UnsupportedOperationException("this reference collection '" + reference + "' isn't supported by this adapter");
            }

            adder.accept(collection);
        }

        return collection;
    }

}
