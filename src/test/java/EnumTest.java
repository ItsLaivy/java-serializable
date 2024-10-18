import codes.laivy.serializable.Serializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class EnumTest {

    @Test
    @DisplayName(value = "Test enum class")
    public void testEnum() {
        for (@NotNull Cool cool : Cool.values()) {
            @NotNull JsonPrimitive expected = new JsonPrimitive(cool.name());
            @NotNull JsonElement actual = Serializer.toJson(cool);

            Assertions.assertEquals(expected, actual, "cannot match '" + expected + "' with '" + actual + "'");
        }
    }

    // Classes

    private enum Cool {

        JAVA,
        SERIALIZABLE,
        IS,
        THE,
        BEST

    }

}
