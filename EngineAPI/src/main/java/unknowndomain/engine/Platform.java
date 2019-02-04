package unknowndomain.engine;

import org.slf4j.Logger;

import java.util.Locale;

/**
 * Engine Platform
 */
public class Platform {

    /**
     * Auto inject
     */
    private static Engine engine;

    public static Engine getEngine() {
        return engine;
    }

    public static Locale getLocale() {
        return Locale.getDefault(); // TODO Game locale
    }

    public static boolean isDevelopmentEnv() {
        return engine.isDevelopmentEnv();
    }

    public static boolean isClient() {
        return engine.isClient(); // TODO
    }

    public static boolean isServer() {
        return engine.isServer();
    }

    public static Logger getLogger() {
        return engine.getLogger();
    }

    public static String getVersion() {
        return "0.0.1";
    }
}
