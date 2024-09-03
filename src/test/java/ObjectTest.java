import codes.laivy.serializable.json.TestJson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.*;
import java.util.Date;
import java.util.Locale;

public final class ObjectTest {

    private static final @NotNull TestJson serializer = new TestJson();
    private static void match(@NotNull Object object) {
        @Nullable JsonElement json;
        @Nullable Object deserialized;

        try {
            json = serializer.serialize(object);
            System.out.println(json);
        } catch (@NotNull InvalidClassException e) {
            throw new RuntimeException("cannot serialize object '" + object + "' from class '" + object.getClass() + "'", e);
        } try {
            deserialized = serializer.deserialize(object.getClass(), json);
        } catch (@NotNull InvalidClassException e) {
            throw new RuntimeException("cannot deserialize json '" + json + "' from object '" + object + "' of class '" + object.getClass() + "'", e);
        }

        System.out.print("Class: '" + object.getClass().getCanonicalName() + "'");
        System.out.print(", Object: '" + object + "'");
        System.out.print(", Json: " + json + "\n");

        Assertions.assertEquals(object, deserialized, "cannot match objects with json '" + json + "'");
    }

    @Test
    @DisplayName("Date Classes (Like OffsetDateTime)")
    public void time() throws InvalidClassException, InstantiationException {
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
    @DisplayName("Locale Classes")
    public void locale() throws InvalidClassException, InstantiationException {
        match(Locale.ENGLISH);
        match(new Locale("pt_br", "brazil", "portuguese"));
    }

}
