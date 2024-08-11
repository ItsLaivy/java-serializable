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
            @NotNull File file = new File(new File(System.getProperty("java.io.tmpdir")), "java-serializable." + (os.contains("win") ? "dll" : "so"));

            try {
                if (!file.exists()) {
                    @Nullable InputStream stream = null;
                    @NotNull String arch = System.getProperty("os.arch");

                    if (os.contains("win")) {
                        if (arch.endsWith("64")) {
                            stream = Allocator.class.getResourceAsStream("/libs/win64.dll");
                        } else if (arch.endsWith("32")) {
                            stream = Allocator.class.getResourceAsStream("/libs/win32.dll");
                        } else {
                            throw new UnsupportedOperationException("the library isn't compatible with your windows OS arch '" + arch + "'");
                        }
                    }

                    if (stream == null) {
                        throw new UnsupportedOperationException("this operating system isn't compatible with the library '" + os + "'");
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

            // Try to load the library again
            System.load(file.toString());
        }
    }

    @ApiStatus.Internal
    public static native <T> @NotNull T allocate(@NotNull Class<?> type);

    // Object

    private Allocator() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
