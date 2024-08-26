package io.github.maninmyvan.dyeableshulkers.utils;

import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public final class Version {
    private final int major, minor, patch;

    @Contract(pure = true)
    public @NotNull String toString() {
        return major + "." + minor + "." + patch;
    }

    @Contract(pure = true)
    public boolean isNewerThan(@NotNull Version v) {
        return isNewerThan(v.major, v.minor, v.patch);
    }

    @Contract(pure = true)
    public boolean isNewerThan(int major, int minor, int patch) {
        return this.major > major || this.major >= major && (this.minor > minor || this.minor >= minor && this.patch > patch);
    }

    @Contract(pure = true)
    public boolean isNewerThanOrEquals(@NotNull Version v) {
        return isNewerThanOrEquals(v.major, v.minor, v.patch);
    }

    @Contract(pure = true)
    public boolean isNewerThanOrEquals(int major, int minor, int patch) {
        return isNewerThan(major, minor, patch) || equals(major, minor, patch);
    }

    @Contract(pure = true)
    public boolean isOlderThan(@NotNull Version v) {
        return isOlderThan(v.major, v.minor, v.patch);
    }

    @Contract(pure = true)
    public boolean isOlderThan(int major, int minor, int patch) {
        return !isNewerThanOrEquals(major, minor, patch);
    }

    @Contract(pure = true)
    public boolean isOlderThanOrEquals(@NotNull Version v) {
        return isOlderThanOrEquals(v.major, v.minor, v.patch);
    }

    @Contract(pure = true)
    public boolean isOlderThanOrEquals(int major, int minor, int patch) {
        return !isNewerThan(major, minor, patch);
    }

    @Contract(pure = true)
    public boolean equals(@NotNull Version v) {
        return equals(v.major, v.minor, v.patch);
    }

    @Contract(pure = true)
    public boolean equals(int major, int minor, int patch) {
        return this.major == major && this.minor == minor && this.patch == patch;
    }
}
