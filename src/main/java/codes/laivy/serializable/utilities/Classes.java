package codes.laivy.serializable.utilities;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

public final class Classes {

    // Static initializers

    public static boolean isConcrete(@NotNull Class<?> reference) {
        if (reference.isArray()) {
            return isConcrete(reference.getComponentType());
        } else if (reference.isPrimitive()) {
            return true;
        }

        return !Modifier.isInterface(reference.getModifiers()) && !Modifier.isAbstract(reference.getModifiers());
    }
    public static void allowModule(@NotNull Class<?> reference, @NotNull Class<?> access) {
        try {
            // Check if modules exists
            @NotNull Class<?> moduleClass = Class.forName("java.lang.Module");

            // Functions
            @NotNull Function<Class<?>, Object> function = new Function<Class<?>, Object>() {
                @SuppressWarnings("JavaReflectionMemberAccess")
                @Override
                public @NotNull Object apply(@NotNull Class<?> a) {
                    try {
                        @NotNull Method method = Class.class.getDeclaredMethod("getModule");
                        return method.invoke(a);
                    } catch (@NotNull NoSuchMethodException e) {
                        throw new RuntimeException("cannot retrieve Class#getModule method", e);
                    } catch (@NotNull InvocationTargetException | @NotNull IllegalAccessException e) {
                        throw new RuntimeException("cannot invoke Class#getModule method", e);
                    }
                }
            };

            // Add opens
            @NotNull Object referenceModule = function.apply(reference);
            @NotNull Object accessModule = function.apply(access);

            @NotNull Method method = moduleClass.getDeclaredMethod("addOpens", String.class, moduleClass);

            method.invoke(referenceModule, reference.getPackage().getName(), accessModule);
        } catch (@NotNull ClassNotFoundException ignore) {
        } catch (@NotNull NoSuchMethodException e) {
            throw new RuntimeException("cannot find Module#addOpens(String, Module) method", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("cannot invoke Module#addOpens(String, Module) method", e);
        }
    }

    // Object

    private Classes() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
