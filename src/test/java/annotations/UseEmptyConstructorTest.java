package annotations;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.UseEmptyConstructor;
import codes.laivy.serializable.json.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public final class UseEmptyConstructorTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();

    @Test
    @DisplayName("Test normally")
    public void normal() {
        @NotNull EmptyConstructor object = new EmptyConstructor();
        @NotNull EmptyConstructor deserialized = Objects.requireNonNull(Serializer.fromJson(object.getClass(), Serializer.toJson(object)));

        Assertions.assertTrue(deserialized.constructed, "The empty constructor hasn't been used!");
    }
    @Test
    @DisplayName("Test not using")
    public void notUsing() {
        @NotNull NotUseEmptyConstructor object = new NotUseEmptyConstructor();
        @NotNull NotUseEmptyConstructor deserialized = Objects.requireNonNull(Serializer.fromJson(object.getClass(), Serializer.toJson(object)));

        Assertions.assertFalse(deserialized.constructed, "The empty constructor has been used even when it doesn't should!");
    }
    @Test
    @DisplayName("Test using fields")
    public void usingFields() {
        @NotNull UsingFields object = new UsingFields();
        @NotNull UsingFields deserialized = Objects.requireNonNull(Serializer.fromJson(object.getClass(), Serializer.toJson(object)));

        Assertions.assertTrue(deserialized.object.constructed, "The empty constructor hasn't been used!");
    }

    // Classes

    @UseEmptyConstructor
    private static final class EmptyConstructor {

        private transient boolean constructed = false;

        private EmptyConstructor() {
            constructed = true;
        }

    }
    private static final class NotUseEmptyConstructor {

        private transient boolean constructed = false;

        private NotUseEmptyConstructor() {
            constructed = true;
        }

    }
    private static final class EmptyConstructorWithoutAnnotation {

        private transient boolean constructed = false;

        private EmptyConstructorWithoutAnnotation() {
            constructed = true;
        }
        private EmptyConstructorWithoutAnnotation(boolean constructed) {
            this.constructed = constructed;
        }

    }

    private static final class UsingFields {

        @UseEmptyConstructor
        private final @NotNull EmptyConstructorWithoutAnnotation object = new EmptyConstructorWithoutAnnotation(false);

        private UsingFields() {
        }

    }

}
