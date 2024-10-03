import codes.laivy.serializable.Allocator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AllocatorTest {

    @Test
    @DisplayName("Test Allocator using JNI")
    public void allocator() {
        @NotNull Cool cool = Allocator.allocate(Cool.class);
    }

    @Test
    @DisplayName("Simple Test Final Field Change using JNI")
    public void simpleSet() throws NoSuchFieldException {
        for (@NotNull String expected : new String[] {
                "Hello!",
                "Cool!",
                "Test!"
        }) {
            @NotNull Cool cool = Allocator.allocate(Cool.class);
            Allocator.setFieldValue(cool.getClass().getDeclaredField("name"), cool, expected);

            Assertions.assertEquals(expected, cool.getName());
        }
    }
    @Test
    @DisplayName("Advanced Test Final Field Change using JNI")
    public void advancedSet() throws IllegalAccessException {
        @NotNull AdvancedCool cool = new AdvancedCool();
        @NotNull Map<Class<?>, Object> expectedMap = new LinkedHashMap<Class<?>, Object>() {{
            put(int.class, Integer.MAX_VALUE);
            put(long.class, Long.MAX_VALUE);
            put(float.class, Float.MAX_VALUE);
            put(double.class, Double.MAX_VALUE);
            put(short.class, Short.MAX_VALUE);
            put(byte.class, Byte.MAX_VALUE);
            put(char.class, Character.MAX_VALUE);
            put(boolean.class, Boolean.TRUE);
        }};

        for (@NotNull Field field : AdvancedCool.class.getDeclaredFields()) {
            @NotNull Object expected = expectedMap.get(field.getType());
            Assertions.assertNotEquals(expected, field.get(cool));

            Allocator.setFieldValue(field, cool, expected);
            Assertions.assertEquals(expected, field.get(cool), "cannot set field type '" + field.getType().getSimpleName() + "' value");
        }
    }

    // Classes

    private static final class Cool {

        private final @NotNull String name;

        public Cool(@NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }

    }
    private static final class AdvancedCool {

        public final int i = 0;
        public final long l = 0L;
        public final float f = 0F;
        public final double d = 0D;
        public final short s = 0;
        public final byte b = 0;
        public final char c = 0;
        public final boolean z = false;

    }

}
