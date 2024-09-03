import codes.laivy.serializable.Allocator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class AllocatorTest {

    @Test
    @DisplayName("Test Allocator using JNI")
    public void allocator() {
        @NotNull Cool cool = Allocator.allocate(Cool.class);
    }

    @Test
    @DisplayName("Test Final Field Change using JNI")
    public void set() throws NoSuchFieldException {
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

}
