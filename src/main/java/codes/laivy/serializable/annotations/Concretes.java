package codes.laivy.serializable.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Concretes {
    @NotNull Concrete[] value();
}
