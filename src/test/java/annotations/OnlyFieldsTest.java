package annotations;

import codes.laivy.serializable.annotations.OnlyFields;
import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Objects;

public final class OnlyFieldsTest {

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

        System.out.println("Color 0: '" + serializer.serialize(object) + "'");
        @NotNull Without deserialized = Objects.requireNonNull(serializer.deserialize(object.getClass(), serializer.serialize(object)));
        System.out.println("Color 1: '" + object.equals(deserialized) + "' '" + getFAlpha(deserialized.color) + "' - " + deserialized.color);
        Assertions.assertEquals(DEFAULT_ALPHA, getFAlpha(deserialized.color), "The falpha field hasn't been included!");
    }

    // Classes

    private static final class Normal {

        @OnlyFields(fields = {"value", "frgbvalue", "fvalue" })
        private final @NotNull Color color;

        private Normal() {
            this.color = new Color(1.0f,1.0f, 1.0f, DEFAULT_ALPHA);
        }

    }
    private static final class Without {

        private final @NotNull Color color;

        private Without() {
            this.color = new Color(1.0f,1.0f, 1.0f, DEFAULT_ALPHA);
            System.out.println("Color 2: " + color);
        }

    }

    // Utilities

    private static float getFAlpha(@NotNull Color color) {
        try {
            @NotNull Field field = color.getClass().getDeclaredField("falpha");
            field.setAccessible(true);

            return field.getFloat(color);
        } catch (@NotNull NoSuchFieldException | @NotNull IllegalAccessException e) {
            throw new RuntimeException("cannot retrieve falpha field from color", e);
        }
    }

}
