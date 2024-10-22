package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.Serializer;
import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.Context;
import codes.laivy.serializable.context.PrimitiveContext;
import codes.laivy.serializable.properties.SerializationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.time.*;
import java.util.Date;

public class TemporalAdapter implements Adapter {

    @Override
    public @NotNull Class<?> @NotNull [] getReferences() {
        return new Class[] {
                Date.class,
                Duration.class,
                Instant.class,
                LocalDate.class,
                LocalDateTime.class,
                MonthDay.class,
                OffsetDateTime.class,
                OffsetTime.class,
                Period.class,
                Year.class,
                YearMonth.class,
                ZoneId.class,
                ZoneOffset.class
        };
    }

    @Override
    public @NotNull Context write(@NotNull Object object, @NotNull Serializer serializer, @Nullable SerializationProperties properties) {
        if (object.getClass() == Date.class) {
            return PrimitiveContext.create(((Date) object).getTime());
        } else {
            return PrimitiveContext.create(object.toString());
        }
    }

    @Override
    public @NotNull Object read(@NotNull Class<?> reference, @NotNull Context context) throws EOFException {
        @NotNull PrimitiveContext object = context.getAsObjectContext();

        if (reference == Date.class) {
            return new Date(object.getAsLong());
        } else if (reference == Duration.class) {
            return Duration.parse(object.getAsString());
        } else if (reference == Instant.class) {
            return Instant.parse(object.getAsString());
        } else if (reference == LocalDate.class) {
            return LocalDate.parse(object.getAsString());
        } else if (reference == LocalDateTime.class) {
            return LocalDateTime.parse(object.getAsString());
        } else if (reference == MonthDay.class) {
            return MonthDay.parse(object.getAsString());
        } else if (reference == OffsetDateTime.class) {
            return OffsetDateTime.parse(object.getAsString());
        } else if (reference == OffsetTime.class) {
            return OffsetTime.parse(object.getAsString());
        } else if (reference == Period.class) {
            return Period.parse(object.getAsString());
        } else if (reference == Year.class) {
            return Year.parse(object.getAsString());
        } else if (reference == YearMonth.class) {
            return YearMonth.parse(object.getAsString());
        } else if (reference == ZoneId.class) {
            return ZoneId.of(object.getAsString());
        } else if (reference == ZoneOffset.class) {
            return ZoneOffset.of(object.getAsString());
        } else {
            throw new UnsupportedOperationException("the reference '" + reference + "' cannot be used at the temporal adapter");
        }
    }

}
