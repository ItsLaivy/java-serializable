package codes.laivy.serializable.adapter.provided;

import codes.laivy.serializable.adapter.Adapter;
import codes.laivy.serializable.context.SerializeInputContext;
import codes.laivy.serializable.context.SerializeOutputContext;
import org.jetbrains.annotations.NotNull;

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
    public void serialize(@NotNull Object object, @NotNull SerializeOutputContext context) {
        if (context.getReference() == Date.class) {
            context.write(((Date) object).getTime());
        } else {
            context.write(object.toString());
        }
    }
    @Override
    public @NotNull Object deserialize(@NotNull SerializeInputContext<?> context) throws EOFException {
        if (context.getReference() == Date.class) {
            return new Date(context.readLong());
        } else if (context.getReference() == Duration.class) {
            return Duration.parse(context.readLine());
        } else if (context.getReference() == Instant.class) {
            return Instant.parse(context.readLine());
        } else if (context.getReference() == LocalDate.class) {
            return LocalDate.parse(context.readLine());
        } else if (context.getReference() == LocalDateTime.class) {
            return LocalDateTime.parse(context.readLine());
        } else if (context.getReference() == MonthDay.class) {
            return MonthDay.parse(context.readLine());
        } else if (context.getReference() == OffsetDateTime.class) {
            return OffsetDateTime.parse(context.readLine());
        } else if (context.getReference() == OffsetTime.class) {
            return OffsetTime.parse(context.readLine());
        } else if (context.getReference() == Period.class) {
            return Period.parse(context.readLine());
        } else if (context.getReference() == Year.class) {
            return Year.parse(context.readLine());
        } else if (context.getReference() == YearMonth.class) {
            return YearMonth.parse(context.readLine());
        } else if (context.getReference() == ZoneId.class) {
            return ZoneId.of(context.readLine());
        } else if (context.getReference() == ZoneOffset.class) {
            return ZoneOffset.of(context.readLine());
        } else {
            throw new UnsupportedOperationException("the reference '" + context.getReference() + "' cannot be used at the temporal adapter");
        }
    }

}
