package utilities;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ArrayUtils {

    // Static initializers

    public static boolean equals(@NotNull Object obj1, @NotNull Object obj2) {
        if (!obj1.getClass().isArray() || !obj2.getClass().isArray()) {
            return false;
        }

        if (obj1 instanceof int[] && obj2 instanceof int[]) {
            return Arrays.equals((int[]) obj1, (int[]) obj2);
        } else if (obj1 instanceof long[] && obj2 instanceof long[]) {
            return Arrays.equals((long[]) obj1, (long[]) obj2);
        } else if (obj1 instanceof double[] && obj2 instanceof double[]) {
            return Arrays.equals((double[]) obj1, (double[]) obj2);
        } else if (obj1 instanceof float[] && obj2 instanceof float[]) {
            return Arrays.equals((float[]) obj1, (float[]) obj2);
        } else if (obj1 instanceof boolean[] && obj2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) obj1, (boolean[]) obj2);
        } else if (obj1 instanceof char[] && obj2 instanceof char[]) {
            return Arrays.equals((char[]) obj1, (char[]) obj2);
        } else if (obj1 instanceof byte[] && obj2 instanceof byte[]) {
            return Arrays.equals((byte[]) obj1, (byte[]) obj2);
        } else if (obj1 instanceof short[] && obj2 instanceof short[]) {
            return Arrays.equals((short[]) obj1, (short[]) obj2);
        }

        assert obj1 instanceof Object[];
        return Arrays.deepEquals((Object[]) obj1, (Object[]) obj2);
    }
    public static @NotNull String toString(@NotNull Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("this object isn't an array!");
        }

        if (array instanceof int[]) {
            return Arrays.toString((int[]) array);
        } else if (array instanceof long[]) {
            return Arrays.toString((long[]) array);
        } else if (array instanceof double[]) {
            return Arrays.toString((double[]) array);
        } else if (array instanceof float[]) {
            return Arrays.toString((float[]) array);
        } else if (array instanceof boolean[]) {
            return Arrays.toString((boolean[]) array);
        } else if (array instanceof char[]) {
            return Arrays.toString((char[]) array);
        } else if (array instanceof byte[]) {
            return Arrays.toString((byte[]) array);
        } else if (array instanceof short[]) {
            return Arrays.toString((short[]) array);
        }

        return Arrays.toString((Object[]) array);
    }

    // Object

    private ArrayUtils() {
        throw new UnsupportedOperationException();
    }

}
