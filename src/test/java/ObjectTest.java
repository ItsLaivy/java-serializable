import codes.laivy.serializable.json.JsonSerializable;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InvalidClassException;
import java.time.*;
import java.util.Date;

public final class ObjectTest {

    private static final @NotNull JsonSerializable serializer = new JsonSerializable();
    private static void match(@NotNull Object object) {
        @NotNull JsonObject json;
        @NotNull Object deserialized;

        try {
            //noinspection deprecation
            json = serializer.serialize(object).getAsJsonObject();
        } catch (@NotNull InvalidClassException e) {
            throw new RuntimeException("cannot serialize object '" + object + "' from class '" + object.getClass() + "'", e);
        } try {
            deserialized = serializer.deserialize(object.getClass(), json);
        } catch (@NotNull InstantiationException | @NotNull InvalidClassException e) {
            throw new RuntimeException("cannot deserialize json '" + json + "' from object '" + object + "' of class '" + object.getClass() + "'", e);
        }

        Assertions.assertEquals(object, deserialized, "cannot match objects with json '" + json + "'");
    }

    @Test
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

}
