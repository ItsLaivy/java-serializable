package codes.laivy.serializable.main;

import org.jetbrains.annotations.NotNull;

public final class Test {
    @NotNull
    private final String name;

    public Test(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }
}
