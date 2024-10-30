package java_serialization;

import codes.laivy.serializable.Serializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public final class ReadResolveTest {

    @Test
    @DisplayName(value = "Main #readResolve method test")
    public void test() {
        @NotNull Main main = new Main();
        Assertions.assertFalse(main.passed);
        main = Objects.requireNonNull(Serializer.fromJson(Main.class, Serializer.toJson(main)));

        Assertions.assertTrue(main.passed, "the #readResolve method haven't been called!");
    }

    // Classes

    private static final class Main {

        private transient boolean passed = false;

        public Main() {
        }

        private @NotNull Main readResolve() {
            passed = true;
            return this;
        }

    }

}
