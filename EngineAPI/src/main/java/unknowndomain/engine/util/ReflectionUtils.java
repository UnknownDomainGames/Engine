package unknowndomain.engine.util;

public final class ReflectionUtils {

    public static String getCallerClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }

    public static StackTraceElement getCallerElement() {
        return Thread.currentThread().getStackTrace()[3];
    }

    private ReflectionUtils() {
    }
}
