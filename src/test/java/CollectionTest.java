import codes.laivy.serializable.annotations.Concrete;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public final class CollectionTest {

    @Test
    @DisplayName("Collections")
    public void collection() {
        ObjectTest.match(new CollectionObject());
    }

    // Classes

    private static final class CollectionObject {

        private static final @NotNull List<String> REFERENCE = Arrays.asList("a", "b", "c");

        @Concrete(type = ArrayList.class)
        private final @NotNull List<String> arrays = REFERENCE;
        private final @NotNull ArrayList<String> array = new ArrayList<>(REFERENCE);
        private final @NotNull LinkedList<String> linked = new LinkedList<>(REFERENCE);
        private final @NotNull Vector<String> vector = new Vector<>(REFERENCE);
        private final @NotNull Stack<String> stack = new Stack<>();
        private final @NotNull CopyOnWriteArrayList<String> copyOnWrite = new CopyOnWriteArrayList<>(REFERENCE);
        private final @NotNull KeySetView<String, Boolean> keySetView = ConcurrentHashMap.newKeySet();
        private final @NotNull ConcurrentSkipListSet<String> concurrentSkipListSet = new ConcurrentSkipListSet<>(REFERENCE);
        private final @NotNull CopyOnWriteArraySet<String> copyOnWriteArray = new CopyOnWriteArraySet<>(REFERENCE);
        private final @NotNull HashSet<String> hash = new HashSet<>(REFERENCE);
        private final @NotNull LinkedHashSet<String> linkedSet = new LinkedHashSet<>(REFERENCE);
        private final @NotNull TreeSet<String> tree = new TreeSet<>(REFERENCE);

        public CollectionObject() {
            keySetView.addAll(REFERENCE);
            stack.addAll(REFERENCE);
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof CollectionObject)) return false;
            @NotNull CollectionObject that = (CollectionObject) object;
            return Objects.equals(arrays, that.arrays) && Objects.equals(array, that.array) && Objects.equals(linked, that.linked) && Objects.equals(vector, that.vector) && Objects.equals(stack, that.stack) && Objects.equals(copyOnWrite, that.copyOnWrite) && Objects.equals(keySetView, that.keySetView) && Objects.equals(concurrentSkipListSet, that.concurrentSkipListSet) && Objects.equals(copyOnWriteArray, that.copyOnWriteArray) && Objects.equals(hash, that.hash) && Objects.equals(linkedSet, that.linkedSet) && Objects.equals(tree, that.tree);
        }
        @Override
        public int hashCode() {
            return Objects.hash(arrays, array, linked, vector, stack, copyOnWrite, keySetView, concurrentSkipListSet, copyOnWriteArray, hash, linkedSet, tree);
        }

        @Override
        public String toString() {
            return "CollectionObject{" +
                    "arrays=" + arrays +
                    ", array=" + array +
                    ", linked=" + linked +
                    ", vector=" + vector +
                    ", stack=" + stack +
                    ", copyOnWrite=" + copyOnWrite +
                    ", keySetView=" + keySetView +
                    ", concurrentSkipListSet=" + concurrentSkipListSet +
                    ", copyOnWriteArray=" + copyOnWriteArray +
                    ", hash=" + hash +
                    ", linkedSet=" + linkedSet +
                    ", tree=" + tree +
                    '}';
        }

    }

}
