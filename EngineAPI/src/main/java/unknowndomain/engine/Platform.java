package unknowndomain.engine;

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

    public boolean isDevelopmentMode() {
        return true;
    }

    public boolean isClient() {
        return true; // TODO
    }

    public boolean isServer() {
        return true; // TODO
    }
}
