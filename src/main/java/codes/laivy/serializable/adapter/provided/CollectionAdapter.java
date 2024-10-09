package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.annotations.Generic;
import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.AttributeList;
import javax.management.relation.RoleList;
import javax.management.relation.RoleUnresolvedList;
import java.io.EOFException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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
                RoleUnresolvedList.class,
                RoleList.class,
                CopyOnWriteArrayList.class,
                AttributeList.class,

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
        @NotNull Class<?>[] references;

        if (context.getGenerics().length > 0) {
            references = Arrays.stream(context.getGenerics()).map(Generic::type).toArray(Class[]::new);
        } else {
            throw new UnsupportedOperationException("a collection requires the @Generic annotation");
        }

        @NotNull Collection collection;

        if (context.getReference() == ARRAYS_ARRAYLIST) {
            @NotNull List<@Nullable Object> list = new LinkedList<>();

            while (true) {
                try {
                    @Nullable Object object = context.readObject(references);
                    list.add(object);
                } catch (@NotNull EOFException ignore) {
                    break;
                }
            }

            return Arrays.asList(list.toArray());
        } else {
            if (context.getReference() == ArrayList.class) {
                collection = new ArrayList<>();
            } else if (context.getReference() == LinkedList.class) {
                collection = new LinkedList<>();
            } else if (context.getReference() == Stack.class) {
                collection = new Stack<>();
            } else if (context.getReference() == RoleUnresolvedList.class) {
                collection = new RoleUnresolvedList();
            } else if (context.getReference() == RoleList.class) {
                collection = new RoleList();
            } else if (context.getReference() == CopyOnWriteArrayList.class) {
                collection = new CopyOnWriteArrayList<>();
            } else if (context.getReference() == AttributeList.class) {
                collection = new AttributeList();
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
            } else {
                throw new UnsupportedOperationException("this reference collection '" + context.getReference() + "' isn't supported by this adapter");
            }

            while (true) {
                try {
                    @Nullable Object object = context.readObject(references);
                    collection.add(object);
                } catch (@NotNull EOFException ignore) {
                    break;
                }
            }
        }

        return collection;
    }

}
