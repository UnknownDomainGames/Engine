package engine.util;

public final class ReflectionUtils {

    public static StackTraceElement getCallerElement() {
        return Thread.currentThread().getStackTrace()[3];
    }

    private ReflectionUtils() {
    }
}
