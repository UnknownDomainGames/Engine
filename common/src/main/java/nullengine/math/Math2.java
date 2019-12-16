package nullengine.math;

public class Math2 {
    public static int ceilPowerOfTwo(int value) {
        if (value <= 0)
            throw new IllegalArgumentException();

        int result = 1;
        while (value > result) {
            result <<= 1;
        }
        return result;
    }

    public static int getUsingBits(int value) {
        int bits = 0;
        while (value != 0) {
            value >>>= 1;
            bits++;
        }
        return bits;
    }

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

    public static float loop(float value, float interval) {
        if (value < 0) {
            value = loop(-value, interval);
            return value == 0 ? 0 : interval - value;
        } else {
            int i = (int) (value / interval);
            return i == 0 ? value : value - i * interval;
        }
    }

    public static float clamp(float val, float min, float max) {
        return Math.min(max, Math.max(val, min));
    }

    public static int clamp(int val, int min, int max) {
        return Math.min(max, Math.max(val, min));
    }

    public static long clamp(long val, long min, long max) {
        return Math.min(max, Math.max(val, min));
    }

    public static double clamp(double val, double min, double max) {
        return Math.min(max, Math.max(val, min));
    }
}
