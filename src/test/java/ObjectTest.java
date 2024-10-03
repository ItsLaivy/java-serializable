import codes.laivy.serializable.json.JsonSerializer;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.ArrayUtils;

import java.time.*;
import java.util.Date;
import java.util.UUID;

public final class ObjectTest {

    private static final @NotNull JsonSerializer serializer = new JsonSerializer();
    private static void match(@NotNull Object object) {
        @Nullable JsonElement json = serializer.serialize(object);

        System.out.print("Class: '" + object.getClass().getCanonicalName() + "'");
        System.out.print(", Object: '" + object + "'");
        System.out.print(", Json: " + json + "\n");

        @Nullable Object deserialized = serializer.deserialize(object.getClass(), json);

        if (object.getClass().isArray()) {
            assert deserialized != null;
            Assertions.assertTrue(ArrayUtils.equals(object, deserialized), "cannot match objects array with json '" + json + "', expected: '" + ArrayUtils.toString(object) + "', current: '" + ArrayUtils.toString(deserialized) + "'");
        } else {
            Assertions.assertEquals(object, deserialized, "cannot match objects with json '" + json + "'");
        }
    }

    @Test
    @DisplayName("Date Classes (Like OffsetDateTime)")
    public void time() {
        match(Duration.ofDays(6).plusHours(6).plusMinutes(6));
        match(OffsetTime.now());
        match(Period.ofDays(5));
        match(YearMonth.now());
        match(Year.now());
        match(MonthDay.now());
        match(ZoneOffset.UTC);
        match(ZoneId.of("UTC"));
        match(Instant.now());
        match(OffsetDateTime.now());
        match(LocalDateTime.now());
        match(LocalDate.now());
        match(new Date());
    }

    @Test
    @DisplayName("Primitive Types")
    public void primitive() {
        // Wrappers
        match(Integer.MAX_VALUE);
        match(Float.MAX_VALUE);
        match(Long.MAX_VALUE);
        match(Double.MAX_VALUE);
        match(Short.MAX_VALUE);
        match(Byte.MAX_VALUE);
        match(Boolean.TRUE);
        match(Character.MAX_VALUE);
    }

    @Test
    @DisplayName("Array Types")
    public void array() {
        // Integer
        match(new int[] { 0, 1, 2, 3, 4, 5 });
        match(new Integer[] { 0, 1, 2, 3, 4, 5 });

        // Float
        match(new float[] { 0f, 1f, 2f, 3f, 4f, 5f });
        match(new Float[] { 0f, 1f, 2f, 3f, 4f, 5f });

        // Long
        match(new long[] { 0L, 1L, 2L, 3L, 4L, 5L });
        match(new Long[] { 0L, 1L, 2L, 3L, 4L, 5L });

        // Double
        match(new double[] { 0D, 1D, 2D, 3D, 4D, 5D });
        match(new Double[] { 0D, 1D, 2D, 3D, 4D, 5D });

        // Short
        match(new short[] { 0, 1, 2, 3, 4, 5 });
        match(new Short[] { 0, 1, 2, 3, 4, 5 });

        // Byte
        match(new byte[] { 0, 1, 2, 3, 4, 5 });
        match(new Byte[] { 0, 1, 2, 3, 4, 5 });

        // Boolean
        match(new boolean[] { true, false, true, false });
        match(new Boolean[] { true, false, true, false });

        // Character
        match(new char[] { 0, 1, 2, 3, 4, 5 });
        match(new Character[] { 0, 1, 2, 3, 4, 5 });

        // Object
        @NotNull UUID[] uuids = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
        match(uuids);
    }

}
