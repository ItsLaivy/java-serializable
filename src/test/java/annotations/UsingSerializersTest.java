package annotations;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.annotations.UsingSerializers;
import codes.laivy.serializable.config.Config;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.PrimitiveContext;
import codes.laivy.serializable.exception.MalformedSerializerException;
import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.util.Objects;

public final class UsingSerializersTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();

    @Test
    @DisplayName("Test normally")
    public void normal() {
        @NotNull Normal normal = new Normal();
        @NotNull JsonElement element = Serializer.toJson(normal);
        @UnknownNullability Normal deserialized = Serializer.fromJson(normal.getClass(), element);

        Assertions.assertEquals(Normal.expected, element, "the expected element was '" + Normal.expected + "' but the current is '" + element + "'.");
        Assertions.assertEquals(normal, deserialized, "the expected object was '" + normal + "' but the current is '" + deserialized + "'");
    }
    @Test
    @DisplayName("Using custom serializer")
    public void custom() {
        @NotNull Custom custom = new Custom();
        @NotNull JsonElement element = Serializer.toJson(custom);
        @UnknownNullability Custom deserialized = Serializer.fromJson(custom.getClass(), element);

        Assertions.assertEquals(Custom.expected, element, "the expected element was '" + Normal.expected + "' but the current is '" + element + "'.");
        Assertions.assertEquals(custom, deserialized, "the expected object was '" + custom + "' but the current is '" + deserialized + "'");
    }
    @Test
    @DisplayName("Using custom serializer from different class")
    public void customDifferentClass() {
        @NotNull CustomDifferentClass different = new CustomDifferentClass();
        @NotNull JsonElement element = Serializer.toJson(different);
        @UnknownNullability CustomDifferentClass deserialized = Serializer.fromJson(different.getClass(), element);

        Assertions.assertEquals(CustomDifferentClass.expected, element, "the expected element was '" + Normal.expected + "' but the current is '" + element + "'.");
        Assertions.assertEquals(different, deserialized, "the expected object was '" + different + "' but the current is '" + deserialized + "'");
    }
    @Test
    @DisplayName("Using the annotation on fields")
    public void usingFields() {
        @NotNull UsingFields deserialized = Objects.requireNonNull(Serializer.fromJson(UsingFields.class, Serializer.toJson(new UsingFields())));

        Assertions.assertEquals(new Normal(), deserialized.normal, "the serializer from the field hasn't been used.");
        Assertions.assertEquals(new Custom(), deserialized.custom, "the serializer from the field hasn't been used.");
        Assertions.assertEquals(new CustomDifferentClass(), deserialized.different, "the serializer from the field hasn't been used.");
    }

    @Test
    @DisplayName("Expect fail without methods")
    public void failWithoutMethods() {
        try {
            @NotNull WithoutMethods without = new WithoutMethods();
            Serializer.toJson(without);

            Assertions.fail("The serializer didn't failed!");
        } catch (@NotNull MalformedSerializerException ignore) {
        }
    }
    @Test
    @DisplayName("Expect fail incompatible methods")
    public void failIncompatibleMethods() {
        try {
            @NotNull IncompatibleMethods incompatible = new IncompatibleMethods();
            Serializer.toJson(incompatible);

            Assertions.fail("The serializer didn't failed!");
        } catch (@NotNull UnsupportedOperationException ignore) {
        }
    }

    // Classes

    @UsingSerializers
    private static final class Normal {

        // Static initializers

        public static final @NotNull JsonElement expected;

        static {
            expected = new JsonPrimitive("Nice and Sharp.");
        }

        // Object

        private final @NotNull String name;

        private Normal(@NotNull String name) {
            this.name = name;
        }
        public Normal() {
            this(expected.getAsString());
        }

        public static @NotNull Context serialize(@NotNull Normal normal, @NotNull Config config) {
            return PrimitiveContext.create(normal.name);
        }
        public static @NotNull Normal deserialize(@NotNull Context context) throws EOFException {
            return new Normal(context.getAsPrimitive().getAsString());
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Normal)) return false;
            @NotNull Normal normal = (Normal) object;
            return Objects.equals(name, normal.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public @NotNull String toString() {
            return name;
        }

    }
    @UsingSerializers(deserialization = "#deserialize01", serialization = "#serialize01")
    private static final class Custom {

        // Static initializers

        public static final @NotNull JsonElement expected;

        static {
            expected = new JsonPrimitive("Customized Serializers");
        }

        // Object

        private final @NotNull String name;

        private Custom(@NotNull String name) {
            this.name = name;
        }
        public Custom() {
            this(expected.getAsString());
        }

        public static @Nullable String serialize01(@NotNull Class<?> reference, @Nullable Object object, @NotNull Config config) {
            if (object == null) {
                return null;
            }

            if (reference == Custom.class) {
                return ((Custom) object).name;
            } else if (reference == CustomDifferentClass.class) {
                return ((CustomDifferentClass) object).name;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        public static @NotNull Object deserialize01(@NotNull Class<?> reference, @NotNull Context context) throws EOFException {
            if (reference == Custom.class) {
                return new Custom(context.getAsPrimitive().getAsString());
            } else if (reference == CustomDifferentClass.class) {
                return new CustomDifferentClass(context.getAsPrimitive().getAsString());
            } else {
                throw new UnsupportedOperationException();
            }
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Custom)) return false;
            @NotNull Custom normal = (Custom) object;
            return Objects.equals(name, normal.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public @NotNull String toString() {
            return name;
        }

    }
    @UsingSerializers(deserialization = "annotations.UsingSerializersTest$Custom#deserialize01", serialization = "annotations.UsingSerializersTest$Custom#serialize01")
    private static final class CustomDifferentClass {

        // Static initializers

        public static final @NotNull JsonElement expected;

        static {
            expected = new JsonPrimitive("Customized Serializers");
        }

        // Object

        private final @NotNull String name;

        private CustomDifferentClass(@NotNull String name) {
            this.name = name;
        }
        public CustomDifferentClass() {
            this(expected.getAsString());
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof CustomDifferentClass)) return false;
            @NotNull CustomDifferentClass normal = (CustomDifferentClass) object;
            return Objects.equals(name, normal.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public @NotNull String toString() {
            return name;
        }

    }

    private static final class UsingFields {

        // Static initializers

        public static int serialize(@NotNull Object object, @NotNull Config config) {
            return 0;
        }
        public static @NotNull Object deserialize(@NotNull Class<?> reference, @NotNull Context context) throws EOFException {
            Assertions.assertEquals(0, context.getAsPrimitive().getAsInteger()); // Just to validate the #serialize method

            if (reference == Normal.class) {
                return new Normal();
            } else if (reference == Custom.class) {
                return new Custom();
            } else if (reference == CustomDifferentClass.class) {
                return new CustomDifferentClass();
            } else {
                throw new UnsupportedOperationException();
            }
        }

        // Object

        @UsingSerializers
        private final @NotNull Normal normal;
        @UsingSerializers
        private final @NotNull Custom custom;
        @UsingSerializers
        private final @NotNull CustomDifferentClass different;

        public UsingFields() {
            this.normal = new Normal("Invalid");
            this.custom = new Custom("Invalid");
            this.different = new CustomDifferentClass("Invalid");
        }

    }

    // Failures

    @UsingSerializers
    private static final class WithoutMethods {
    }
    @UsingSerializers
    private static final class IncompatibleMethods {

        private final @NotNull String ignore = "Ignore me! I've added this field so Intellij IDEA doesn't recognizes this class as an utility class.";

        public static @NotNull String serialize(@NotNull String object) {
            return object;
        }
        public static @NotNull String deserialize(@NotNull Context context) {
            return "invalid";
        }
    }

}
