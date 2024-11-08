package annotations;

import codes.laivy.serializable.Allocator;
import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.serializers.EnheritSerializers;
import codes.laivy.serializable.annotations.serializers.Serializers;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.MapContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EnheritSerializersTest {

    @Test
    @DisplayName("Test normally")
    public void normally() {
        @NotNull Main main = new Normally();
        @NotNull JsonElement element = Serializer.toJson(main);

        Assertions.assertTrue(element.isJsonPrimitive(), "the json element returned by this serializer must be a primitive!");

        @NotNull JsonPrimitive primitive = element.getAsJsonPrimitive();
        Assertions.assertEquals(main.getClass().getName(), primitive.getAsString(), "the Main's serializer hasn't been used.");
        Assertions.assertEquals(main, Serializer.fromJson(Main.class, element), "the deserialized elements aren't the same.");
    }

    @Test
    @DisplayName("Test recusirvely")
    public void recursively() {
        @NotNull Main main = new Recursively();
        @NotNull JsonElement element = Serializer.toJson(main);

        Assertions.assertTrue(element.isJsonPrimitive(), "the json element returned by this serializer must be a primitive!");

        @NotNull JsonPrimitive primitive = element.getAsJsonPrimitive();
        Assertions.assertEquals(main.getClass().getName(), primitive.getAsString(), "the Main's serializer hasn't been used.");
        Assertions.assertEquals(main, Serializer.fromJson(Main.class, element), "the deserialized elements aren't the same.");
    }

    // Classes

    @Serializers
    private static abstract class Main {

        private static @NotNull Class<? extends Main> serialize(@NotNull Main main) {
            System.out.println("A");
            return main.getClass();
        }
        private static @NotNull Main deserialize(@NotNull Class<? extends Main> reference) {
            return Allocator.allocate(reference);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null) return false;
            return obj.getClass() == this.getClass();
        }

    }

    @EnheritSerializers
    private static class Normally extends Main {
    }
    @EnheritSerializers
    private static final class Recursively extends Normally {
    }

}
