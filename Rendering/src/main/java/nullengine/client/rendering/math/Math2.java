package nullengine.client.rendering.math;

public class Math2 {

    public static int ceil(int value, int interval) {
        if (value == 0)
            return 0;
        if (interval == 0)
            return 0;
        if (value < 0)
            interval *= -1;
        int mod = value % interval;
        return mod == 0 ? value : value + interval - mod;
    }

    public static int ceilPowerOfTwo(int value) {
        if (value <= 0)
            throw new IllegalArgumentException();

        int result = 1;
        while (value > result) {
            result <<= 1;
        }
        return result;
    }
}
