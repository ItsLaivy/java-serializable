package java_serialization;

import codes.laivy.serializable.Serializer;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public final class WriteReplaceTest {

    @Test
    @DisplayName(value = "Main #writeReplace method test")
    public void test() {
        @NotNull Main main = new Main();
        Assertions.assertFalse(main.passed);
        @NotNull JsonObject json = Serializer.toJson(main).getAsJsonObject();
        Assertions.assertTrue(json.get("passed").getAsBoolean(), "the #writeReplace method haven't been called!");

        main = Objects.requireNonNull(Serializer.fromJson(Main.class, json));
        Assertions.assertTrue(main.passed);
    }

    // Classes

    private static final class Main {

        private boolean passed = false;

        public Main() {
        }

        private @NotNull Main writeReplace() {
            passed = true;
            return this;
        }

    }
    
}
