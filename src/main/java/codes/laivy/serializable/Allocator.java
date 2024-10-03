package codes.laivy.serializable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Allocator {

    // Static initializers

    private static final @NotNull String VERSION;
    private static final @NotNull Map<Class<?>, Class<?>> WRAPPERS = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(char.class, Character.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(short.class, Short.class);
        put(void.class, Void.class);
    }};

    static {
        try (@NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Allocator.class.getResourceAsStream("/version"), "cannot retrieve version stream")))) {
            VERSION = reader.readLine();
        } catch (@NotNull IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            // Get file name without the extension and load the library
            System.loadLibrary("laivy/serializable");
        } catch (@NotNull UnsatisfiedLinkError ignore) { // Library doesn't exist
            @NotNull String os = System.getProperty("os.name").toLowerCase();
            @NotNull String arch = System.getProperty("os.arch");

            @NotNull File file = new File(new File(System.getProperty("java.io.tmpdir")), "java-serializable-" + VERSION + "." + (os.contains("win") ? "dll" : os.contains("mac") ? "dylib" : "so"));

            try {
                if (!file.exists()) {
                    @Nullable InputStream stream = null;

                    if (os.contains("win")) {
                        if (arch.contains("64")) {
                            stream = Allocator.class.getResourceAsStream("/libs/win64.dll");
                        } else if (arch.contains("32")) {
                            stream = Allocator.class.getResourceAsStream("/libs/win32.dll");
                        }
                    } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                        if (arch.contains("64")) {
                            stream = Allocator.class.getResourceAsStream("/libs/linux64.so");
                        } else if (arch.contains("32")) {
                            stream = Allocator.class.getResourceAsStream("/libs/linux32.so");
                        }
                    } else if (os.contains("mac")) {
                        if (arch.equalsIgnoreCase("x86_64")) {
                            stream = Allocator.class.getResourceAsStream("/libs/macos_x86_64.dylib");
                        } else if (arch.equalsIgnoreCase("aarch64")) {
                            stream = Allocator.class.getResourceAsStream("/libs/macos_arm64.dylib");
                        }
                    }

                    if (stream == null) {
                        throw new UnsupportedOperationException("this operating system isn't compatible with the library '" + os + "' with arch '" + arch + "'");
                    }

                    try (@NotNull OutputStream out = Files.newOutputStream(file.toPath())) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = stream.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }
                }
            } catch (@NotNull IOException e) {
                throw new RuntimeException("cannot create library temporary file '" + file + "'", e);
            }

            try {
                // Try to load the library again
                System.load(file.toString());
            } catch (@NotNull Throwable throwable) {
                throw new RuntimeException("cannot load library using os '" + os + "' with arch '" + arch + "'", throwable);
            }
        }
    }

    /**
     * Allocates and returns a new instance of the specified class without invoking any constructor.
     * <p>
     * This method bypasses the usual constructor invocation process and directly allocates memory for a new object.
     * This can be useful in scenarios where object creation needs to occur without running any initialization code,
     * such as in serialization frameworks or low-level libraries
     * where precise control over object creation is required.
     *
     * @param type the class of the object to be created; must not be {@code null}.
     * @param <T> the type of the object to be created.
     * @return a new instance of the specified class, never {@code null}.
     * @throws NullPointerException if {@code type} is {@code null}.
     * @throws IllegalArgumentException if the class cannot be instantiated, such as for interfaces, abstract classes, or primitives.
     */
    @ApiStatus.Internal
    public static native <T> @NotNull T allocate(@NotNull Class<T> type);

    /**
     * Sets the value of a final field, bypassing the usual immutability constraints.
     * <p>
     * This method allows modifying the value of a final field even after the field has been initialized.
     * It is typically used in scenarios where fields need to be altered in frameworks that require
     * deep integration with Java's reflection mechanisms,
     * such as dependency injection, testing, or mocking libraries.
     * <p>
     * Caution should be exercised when using this method, as it breaks the immutability guarantee of final fields,
     * which can lead to unexpected behavior or security vulnerabilities if used improperly.
     *
     * @param field the field to modify; must not be {@code null}.
     * @param instance the instance containing the field to modify, or {@code null} if the field is static.
     * @param object the new value to set for the field; can be {@code null} if the field type allows it.
     */
    @ApiStatus.Internal
    public static native void setFieldValue(@NotNull Field field, @Nullable Object instance, @Nullable Object object);

    @ApiStatus.Internal
    public static boolean isAssignableFromIncludingPrimitive(@NotNull Class<?> c1, @NotNull Class<?> c2) {
        // Debug
        //System.out.println("'" + c1 + "' - '" + c2 + "': " + ((c1 == c2) || (c1.isAssignableFrom(c2)) || WRAPPERS.containsKey(c1) && WRAPPERS.get(c1).isAssignableFrom(c2)) + " | '" + (c1 == c2) + "' - '" + c1.isAssignableFrom(c2) + "' - '" + (WRAPPERS.containsKey(c1) && WRAPPERS.get(c1).isAssignableFrom(c2)) + "'");

        if (c1 == c2) {
            return true;
        } else if (c1.isAssignableFrom(c2)) {
            return true;
        } else {
            return WRAPPERS.containsKey(c1) && WRAPPERS.get(c1).isAssignableFrom(c2);
        }
    }

    // Object

    private Allocator() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}