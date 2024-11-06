package annotations;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.Concrete;
import codes.laivy.serializable.exception.IncompatibleReferenceException;
import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ConcreteTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();

    @Test
    @DisplayName("Test normally")
    public void normal() {
        @NotNull Normal normal = new Normal();
        @NotNull JsonElement element = Serializer.toJson(normal);

        Assertions.assertEquals(normal, Serializer.fromJson(normal.getClass(), element));
    }
    @Test
    @DisplayName("Test generic")
    public void generic() {
        @NotNull Generic generic = new Generic();
        @NotNull JsonElement element = Serializer.toJson(generic);
        @Nullable Generic result = Serializer.fromJson(generic.getClass(), element);

        Assertions.assertEquals(generic, result, "Cannot deserialize '" + element + "' into expected generic: " + result);
    }
    @Test
    @DisplayName("Test declared at class")
    public void concreteDeclaredAtClass() {
        @NotNull Gender gender = new Male();
        @NotNull JsonElement element = Serializer.toJson(gender);
        @Nullable Gender result = Serializer.fromJson(Gender.class, element);

        Assertions.assertEquals(gender, result, "Cannot deserialize '" + element + "' into expected generic: " + result);
    }

    @Test
    @DisplayName("Expect fail missing generic")
    public void failMissingGeneric() {
        try {
            @NotNull FailWithoutConcrete generic = new FailWithoutConcrete();
            @NotNull JsonElement element = Serializer.toJson(generic);

            Assertions.assertEquals(generic, Serializer.fromJson(generic.getClass(), element));
            Assertions.fail("Didn't failed with the missing @Concrete annotation");
        } catch (@NotNull IncompatibleReferenceException ignore) {
        }
    }

    // Classes

    @Concrete(type = Female.class)
    @Concrete(type = Male.class)
    // Let's suppose that class is sealed
    private static abstract class Gender {
    }
    private static final class Female extends Gender {
        private final @NotNull String female = "It's a female";

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            return object instanceof Female;
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(female);
        }

        @Override
        public @NotNull String toString() {
            return "Female{" +
                    "female='" + female + '\'' +
                    '}';
        }


    }
    private static final class Male extends Gender {
        private final @NotNull String male = "It's a male";

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            return object instanceof Male;
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(male);
        }

        @Override
        public @NotNull String toString() {
            return "Male{" +
                    "male='" + male + '\'' +
                    '}';
        }

    }

    private static final class Normal {

        @Concrete(type = HashSet.class)
        private final @NotNull Set<String> set;

        private Normal() {
            this.set = new HashSet<>();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Normal)) return false;
            @NotNull Normal normal = (Normal) object;
            return Objects.equals(set, normal.set);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(set);
        }

    }
    private static final class Generic {

        @Concrete(type = HashSet.class)
        private final @NotNull Set<@Concrete(type = Dog.class) @Concrete(type = Cat.class) Animal> set;

        private Generic() {
            this.set = new HashSet<>();
            this.set.add(new Cat());
            this.set.add(new Cat());
            this.set.add(new Dog());
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Generic)) return false;
            Generic generic = (Generic) object;
            return Objects.equals(set, generic.set);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(set);
        }

        @Override
        public @NotNull String toString() {
            return "Generic{" +
                    "set=" + set +
                    '}';
        }

    }

    // Failures

    @SuppressWarnings("FieldCanBeLocal")
    private static final class FailWithoutConcrete {

        private final @NotNull Set<Animal> set = new HashSet<>();

        private FailWithoutConcrete() {
        }

    }

    // Utilities classes

    private static abstract class Animal {
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static class Cat extends Animal {

        private final @NotNull String cat = "Miau";

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Cat)) return false;
            @NotNull Cat cat1 = (Cat) object;
            return true;
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(cat);
        }
    }
    @SuppressWarnings("FieldCanBeLocal")
    private static class Dog extends Animal {

        private final @NotNull String dog = "Au au";

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Dog)) return false;
            @NotNull Dog dog1 = (Dog) object;
            return true;
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(dog);
        }
    }

}
