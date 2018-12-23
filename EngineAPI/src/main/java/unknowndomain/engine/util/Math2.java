package unknowndomain.engine.util;

public final class Math2 {
    private Math2() {
    }

    public static int roundUp(int value, int interval) {
        if (value == 0)
            return 0;
        if (interval == 0)
            return 0;
        if (value < 0)
            interval *= -1;
        int mod = value % interval;
        return mod == 0 ? value : value + interval - mod;
    }

    public static float roundUp(float value, float interval) {
        if (value == 0)
            return 0;
        if (interval == 0)
            return 0;
        if (value < 0)
            interval *= -1;
        int mod = (int) (value / interval);
        return mod == 0 ? value : value - mod * interval;
    }
}
