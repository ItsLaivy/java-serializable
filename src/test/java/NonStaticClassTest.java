import codes.laivy.serializable.Serializer;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class NonStaticClassTest {

    @Test
    @DisplayName(value = "Test non static inner class")
    public void testInnerClass() {
        @NotNull Inner inner = new Inner();
        @NotNull JsonObject object = Serializer.toJson(inner).getAsJsonObject();

        Assertions.assertFalse(object.has("this$0"), "inner instance field value detected");
    }

    // Classes

    private final class Inner {
    }

}
