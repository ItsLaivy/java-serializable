import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public final class NonStaticClassTest {

    @Test
    @DisplayName(value = "Check if inner class data has this$0 value")
    public void checkValue() {
        @NotNull Inner inner = new Inner();
        @NotNull JsonObject object = Serializer.toJson(inner).getAsJsonObject();

        Assertions.assertFalse(object.has("this$0"), "inner instance field value detected");
    }

    @Test
    @DisplayName(value = "Test non static inner class with configuration")
    public void testInnerClassWithConfig() {
        @NotNull Inner inner = new Inner();
        Assertions.assertEquals(0, inner.getNumber(), "cannot match reference from inner created object");

        @NotNull JsonObject object = Serializer.toJson(inner).getAsJsonObject();
        inner = Objects.requireNonNull(Serializer.fromJson(Inner.class, object, Config.builder(JsonSerializer.getInstance(), Inner.class).outerInstance(this).build()));

        Assertions.assertEquals(0, inner.getNumber(), "cannot match reference from inner deserialized object");
    }
    @Test
    @DisplayName(value = "Test non static inner class with field")
    public void testInnerClassWithField() {
        @NotNull Fields fields = new Fields();
        Assertions.assertEquals(0, fields.inner.getNumber(), "cannot match reference from inner created object");

        @NotNull JsonObject object = Serializer.toJson(fields).getAsJsonObject();
        fields = Objects.requireNonNull(Serializer.fromJson(Fields.class, object));

        Assertions.assertEquals(0, fields.inner.getNumber(), "cannot match reference from inner deserialized object");
    }

    // Classes

    private int getNumber() {
        return 0;
    }

    private final class Inner {

        public int getNumber() {
            return NonStaticClassTest.this.getNumber();
        }

    }

    private static final class Fields {

        private final @NotNull Inner inner = new Inner();

        private int getNumber() {
            return 0;
        }

        // Classes

        private final class Inner {
            private int getNumber() {
                return Fields.this.getNumber();
            }
        }

    }

}
