import codes.laivy.serializable.Allocator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class AllocatorTest {

    @Test
    @DisplayName("Test Allocator JNI")
    public void allocator() {
        @NotNull Cool cool = Allocator.allocate(Cool.class);
    }

    // Classes

    private static final class Cool {
    }

}
