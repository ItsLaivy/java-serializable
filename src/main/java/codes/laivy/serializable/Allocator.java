package codes.laivy.serializable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public final class Allocator {

    // Static initializers

    static {
        try {
            // Get file name without the extension and load the library
            System.loadLibrary("laivy/serializable");
        } catch (@NotNull UnsatisfiedLinkError ignore) { // Library doesn't exist
            @NotNull String os = System.getProperty("os.name").toLowerCase();
            @NotNull String arch = System.getProperty("os.arch");

            // todo: improve this caching
            @NotNull File file = new File(new File(System.getProperty("java.io.tmpdir")), "java-serializable." + (os.contains("win") ? "dll" : "so"));

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

    @ApiStatus.Internal
    public static native <T> @NotNull T allocate(@NotNull Class<?> type);

    // Object

    private Allocator() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
