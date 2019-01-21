package unknowndomain.engine;

import org.slf4j.Logger;

import java.util.Locale;

/**
 * Engine Platform
 */
public class Platform {

    private static Engine engine; // TODO inject

    public static Engine getEngine() {
        return engine;
    }

    public static Locale getLocale() {
        return Locale.getDefault(); // TODO Game locale
    }

    public static boolean isDevelopmentMode() {
        return true;
    }

    public static boolean isClient() {
        return getEngine().isClient(); // TODO
    }

    public static boolean isServer() {
        return getEngine().isServer();
    }

    public static Logger getLogger() {
        return Engine.getLogger();
    }
}
