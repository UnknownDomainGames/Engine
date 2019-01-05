package unknowndomain.engine.client.util;

public class Utils {

    public static float middleValue(float arg0, float arg1, float arg2) {
        return Math.min(Math.max(arg0, arg1), Math.max(arg1, arg2));
    }
}
