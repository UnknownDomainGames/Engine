package unknowndomain.engine.util;

public class Maths {
    public static int sign(short x) {
        return (x >> 31) | (-x >>> 31);
    }

    public static int sign(byte x) {
        return (x >> 31) | (-x >>> 31);
    }

    public static int sign(int x) {
        return (x >> 31) | (-x >>> 31);
    }

    public static long sign(long x) {
        return (x >> 31) | (-x >>> 31);
    }

    /**
     * difference or zero
     *
     * @param x
     * @param y
     * @return
     */
    public static int doz(int x, int y) {
        return (x - y) & ~((x - y) >> 31);
    }

    public static int max(int x, int y) {
        return x - ((x - y) & (x - y) >> 31);
    }

    public static int min(int x, int y) {
        return y + ((x - y) & (x - y) >> 31);
    }

}
