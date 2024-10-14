package annotations;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.BypassTransient;
import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BypassTransientTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();

    @Test
    @DisplayName("Test with transient fields")
    public void withTransients() {
        @NotNull TestWithTransient test = new TestWithTransient();
        @NotNull JsonObject object = Serializer.toJson(test).getAsJsonObject();
        @NotNull JsonObject field = object.getAsJsonObject("field");

        Assertions.assertTrue(field.has("ignoreMe"), "cannot find transient field '" + object + "'");
    }
    @Test
    @DisplayName("Test without transient fields")
    public void withoutTransients() {
        @NotNull TestWithoutTransient test = new TestWithoutTransient();
        @NotNull JsonObject object = Serializer.toJson(test).getAsJsonObject();
        @NotNull JsonObject field = object.getAsJsonObject("field");

        Assertions.assertFalse(field.has("ignoreMe"), "the transient field is here '" + object + "'");
    }

    // Classes

    private static final class TestWithTransient {

        @BypassTransient
        private final @NotNull Default field = new Default();

    }
    private static final class TestWithoutTransient {

        private final @NotNull Default field = new Default();

    }

    private static final class Default {

        private final @NotNull String key = "Laivy is cool.";
        private final transient @NotNull String ignoreMe = "I'm here!";

    }

}
