package annotations;

import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public final class ConcreteTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();

    @Test
    @DisplayName("Test normally")
    public void normal() {
        @NotNull Normal normal = new Normal();
        @NotNull JsonElement element = serializer.serialize(normal);

        Assertions.assertEquals(normal, serializer.deserialize(normal.getClass(), element));
    }

    // Classes

    private static final class Normal {

        @Concrete(type = HashSet.class)
        private final @NotNull Set<String> set;

        private Normal() {
            this.set = new HashSet<>();
        }

    }

}
