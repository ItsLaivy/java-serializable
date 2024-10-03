package annotations;

import codes.laivy.serializable.annotations.ExcludeFields;
import codes.laivy.serializable.annotations.OnlyFields;
import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Objects;

import static annotations.OnlyFieldsTest.getFAlpha;

public final class ExcludeFieldsTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();
    private static final float DEFAULT_ALPHA = 0.8f;

    @Test
    @DisplayName("Test normally")
    public void normal() {
        @NotNull Normal object = new Normal();
        Assertions.assertEquals(DEFAULT_ALPHA, getFAlpha(object.color));

        object = Objects.requireNonNull(serializer.deserialize(object.getClass(), serializer.serialize(object)));
        Assertions.assertEquals(0.0f, getFAlpha(object.color), "The falpha field hasn't been excluded!");
    }
    @Test
    @DisplayName("Test without the annotation")
    public void withoutAnnotation() {
        @NotNull Without object = new Without();
        Assertions.assertEquals(DEFAULT_ALPHA, getFAlpha(object.color));

        @NotNull Without deserialized = Objects.requireNonNull(serializer.deserialize(object.getClass(), serializer.serialize(object)));
        Assertions.assertEquals(DEFAULT_ALPHA, getFAlpha(deserialized.color), "The falpha field hasn't been included!");
    }
    @Test
    @DisplayName("Test priority using @OnlyFields and @ExcludeFields")
    public void priority() {
        @NotNull Priority object = new Priority();
        Assertions.assertEquals(DEFAULT_ALPHA, getFAlpha(object.color));

        @NotNull Priority deserialized = Objects.requireNonNull(serializer.deserialize(object.getClass(), serializer.serialize(object)));
        Assertions.assertEquals(DEFAULT_ALPHA, getFAlpha(deserialized.color));
    }

    // Classes

    private static final class Normal {

        @ExcludeFields(fields = { "falpha" })
        private final @NotNull Color color;

        private Normal() {
            this.color = new Color(1.0f,1.0f, 1.0f, DEFAULT_ALPHA);
        }

    }
    private static final class Without {

        private final @NotNull Color color;

        private Without() {
            this.color = new Color(1.0f,1.0f, 1.0f, DEFAULT_ALPHA);
        }

    }
    private static final class Priority {

        @ExcludeFields(fields = { "value", "frgbvalue", "fvalue", "falpha" })
        @OnlyFields(fields = { "value", "frgbvalue", "fvalue", "falpha" })
        private final @NotNull Color color;

        private Priority() {
            this.color = new Color(1.0f,1.0f, 1.0f, DEFAULT_ALPHA);
        }

    }

}
