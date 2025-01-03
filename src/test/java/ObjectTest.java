import codes.laivy.serializable.Serializer;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.ArrayUtils;

import java.time.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

public final class ObjectTest {

    public static void match(@NotNull Object object) {
        @Nullable JsonElement json = Serializer.toJson(object);
        @Nullable Throwable throwable = null;

        try {
            @Nullable Object deserialized = Serializer.fromJson(object.getClass(), json);

            if (object.getClass().isArray()) {
                assert deserialized != null;
                Assertions.assertTrue(ArrayUtils.equals(object, deserialized), "cannot match objects array with json '" + json + "', expected: '" + ArrayUtils.toString(object) + "', current: '" + ArrayUtils.toString(deserialized) + "'");
            } else {
                Assertions.assertEquals(object, deserialized, "cannot match objects with json '" + json + "'");
            }
        } catch (@NotNull Throwable t) {
            throwable = t;
        } finally {
            if (throwable != null) {
                System.out.print("\033[31mERROR:   \033[0m");
            } else {
                System.out.print("\033[32mSUCCESS: \033[0m");
            }

            System.out.print("Class: '" + object.getClass().getCanonicalName() + "'");
            System.out.print(", Object: '" + (object.getClass().isArray() ? ArrayUtils.toString(object) : object) + "'");
            System.out.print(", Json: " + json + "\n");

            if (throwable != null) {
                Assertions.fail(throwable);
            }
        }
    }

    @Test
    @DisplayName("Date Classes (Like OffsetDateTime)")
    public void time() {
        match(Duration.ofDays(6).plusHours(6).plusMinutes(6));
        match(OffsetTime.now());
        match(Period.ofDays(5));
        match(YearMonth.now());
        match(Year.now());
        match(MonthDay.now());
        match(ZoneOffset.UTC);
        match(ZoneId.of("UTC"));
        match(Instant.now());
        match(OffsetDateTime.now());
        match(LocalDateTime.now());
        match(LocalDate.now());
        match(new Date());
    }

    @Test
    @DisplayName("Primitive Types")
    public void primitive() {
        // Wrappers
        match(Integer.MAX_VALUE);
        match(Float.MAX_VALUE);
        match(Long.MAX_VALUE);
        match(Double.MAX_VALUE);
        match(Short.MAX_VALUE);
        match(Byte.MAX_VALUE);
        match(Boolean.TRUE);
        match(Character.MAX_VALUE);
    }

    @Test
    @DisplayName("Array Types")
    public void array() {
        // Integer
        match(new int[] { 0, 1, 2, 3, 4, 5 });
        match(new Integer[] { 0, 1, 2, 3, 4, 5 });

        // Float
        match(new float[] { 0f, 1f, 2f, 3f, 4f, 5f });
        match(new Float[] { 0f, 1f, 2f, 3f, 4f, 5f });

        // Long
        match(new long[] { 0L, 1L, 2L, 3L, 4L, 5L });
        match(new Long[] { 0L, 1L, 2L, 3L, 4L, 5L });

        // Double
        match(new double[] { 0D, 1D, 2D, 3D, 4D, 5D });
        match(new Double[] { 0D, 1D, 2D, 3D, 4D, 5D });

        // Short
        match(new short[] { 0, 1, 2, 3, 4, 5 });
        match(new Short[] { 0, 1, 2, 3, 4, 5 });

        // Byte
        match(new byte[] { 0, 1, 2, 3, 4, 5 });
        match(new Byte[] { 0, 1, 2, 3, 4, 5 });

        // Boolean
        match(new boolean[] { true, false, true, false });
        match(new Boolean[] { true, false, true, false });

        // Character
        match(new char[] { 0, 1, 2, 3, 4, 5 });
        match(new Character[] { 0, 1, 2, 3, 4, 5 });

        // Object with adapter
        @NotNull UUID[] uuids = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
        match(uuids);

        // Object without adapter
        @NotNull TestClass[] tests = new TestClass[] { new TestClass("Laivy", 19), new TestClass("Meruhz", 17) };
        match(tests);
    }

    // Classes

    private static final class TestClass {

        private final @NotNull LinkedList<String> values = new LinkedList<>();
        private final @NotNull String name;
        private final int age;

        public TestClass(@NotNull String name, int age) {
            this.name = name;
            this.age = age;

            this.values.add("One");
            this.values.add("Two");
            this.values.add("Three");
        }

        public @NotNull String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof TestClass)) return false;
            @NotNull TestClass testClass = (TestClass) object;
            return getAge() == testClass.getAge() && Objects.equals(values, testClass.values) && Objects.equals(getName(), testClass.getName());
        }
        @Override
        public int hashCode() {
            return Objects.hash(values, getName(), getAge());
        }

        @Override
        public @NotNull String toString() {
            return "TestClass{" +
                    "values=" + values +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

    }

}
