package annotations;

import codes.laivy.serializable.Allocator;
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
        @NotNull EmptyConstructor deserialized = Objects.requireNonNull(serializer.deserialize(object.getClass(), serializer.serialize(object)));

        Assertions.assertTrue(deserialized.constructed, "The empty constructor hasn't been used!");
    }
    @Test
    @DisplayName("Test using fields")
    public void usingFields() {
        @NotNull UsingFields object = new UsingFields();
        @NotNull UsingFields deserialized = Objects.requireNonNull(serializer.deserialize(object.getClass(), serializer.serialize(object)));

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
    private static final class EmptyConstructorWithoutAnnotation {

        private transient boolean constructed = false;

        private EmptyConstructorWithoutAnnotation() {
            constructed = true;
        }

    }

    private static final class UsingFields {

        @UseEmptyConstructor
        private final @NotNull EmptyConstructorWithoutAnnotation object = Allocator.allocate(EmptyConstructorWithoutAnnotation.class);

        private UsingFields() {
        }

    }

}
