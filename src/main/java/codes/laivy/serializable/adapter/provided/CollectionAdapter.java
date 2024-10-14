package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.annotations.Concretes;
import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
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
    public void serialize(@NotNull Object object, @NotNull SerializeOutputContext context) {
        if (object instanceof Collection<?>) {
            @NotNull Collection<?> collection = (Collection<?>) object;

            for (@Nullable Object element : collection) {
                context.write(element);
            }
        } else {
            throw new UnsupportedOperationException("this adapter only supports some collection types!");
        }
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @NotNull Object deserialize(@NotNull SerializeInputContext context) throws EOFException {
        @NotNull List<Class<?>> temp = new LinkedList<>();
        int fromFields;

        if (context.getAnnotatedType() != null) {
            @NotNull AnnotatedType type = context.getAnnotatedType();
            @NotNull List<AnnotatedType> types = new LinkedList<>();

            // Função auxiliar para adicionar tipos concretos
            Consumer<AnnotatedType> addConcreteTypes = (t) -> {
                if (t.getType() instanceof Class && isConcrete((Class<?>) t.getType())) {
                    temp.add((Class<?>) t.getType());
                }

                temp.addAll(Arrays.stream(t.getAnnotationsByType(Concrete.class)).map(Concrete::type).collect(Collectors.toList()));
                temp.addAll(Arrays.stream(t.getAnnotationsByType(Concretes.class)).flatMap(concretes -> Arrays.stream(concretes.value())).map(Concrete::type).collect(Collectors.toList()));
            };

            // Adiciona a partir dos campos nativos
            addConcreteTypes.accept(type);
            fromFields = temp.size();

            if (type instanceof AnnotatedParameterizedType) {
                types.addAll(Arrays.asList(((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments()));
            }

            while (!types.isEmpty()) {
                type = types.remove(0);

                if (type instanceof AnnotatedParameterizedType) {
                    types.addAll(Arrays.asList(((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments()));
                }
                addConcreteTypes.accept(type);
            }
        } else {
            throw new UnsupportedOperationException("a collection requires an annotated type");
        }

        @NotNull Collection collection;

        // Functions

        @NotNull Consumer<Collection<Object>> adder = objects -> {
            @NotNull Class<?>[] references = temp.toArray(new Class[0]);
            references = Arrays.copyOfRange(references, fromFields, references.length);

            while (true) {
                try {
                    @Nullable Object object = context.readObject(references);
                    objects.add(object);
                } catch (@NotNull EOFException ignore) {
                    break;
                } catch (@NotNull Throwable throwable) {
                    throw new RuntimeException("cannot deserialize object to collection '" + objects.getClass().getName() + "' using references " + Arrays.toString(references), throwable);
                }
            }
        };

        // Read
        if (context.getReference() == ARRAYS_ARRAYLIST) {
            @NotNull List<@Nullable Object> list = new LinkedList<>();
            adder.accept(list);

            return Arrays.asList(list.toArray());
        } else {
            if (context.getReference() == ArrayList.class) {
                collection = new ArrayList<>();
            } else if (context.getReference() == LinkedList.class) {
                collection = new LinkedList<>();
            } else if (context.getReference() == Stack.class) {
                collection = new Stack<>();
            } else if (context.getReference() == CopyOnWriteArrayList.class) {
                collection = new CopyOnWriteArrayList<>();
            } else if (context.getReference() == KeySetView.class) {
                collection = ConcurrentHashMap.newKeySet();
            } else if (context.getReference() == ConcurrentSkipListSet.class) {
                collection = new ConcurrentSkipListSet();
            } else if (context.getReference() == CopyOnWriteArraySet.class) {
                collection = new CopyOnWriteArraySet();
            } else if (context.getReference() == HashSet.class) {
                collection = new HashSet();
            } else if (context.getReference() == LinkedHashSet.class) {
                collection = new LinkedHashSet();
            } else if (context.getReference() == TreeSet.class) {
                collection = new TreeSet();
            } else if (context.getReference() == Vector.class) {
                collection = new Vector();
            } else {
                throw new UnsupportedOperationException("this reference collection '" + context.getReference() + "' isn't supported by this adapter");
            }

            adder.accept(collection);
        }

        return collection;
    }

}
