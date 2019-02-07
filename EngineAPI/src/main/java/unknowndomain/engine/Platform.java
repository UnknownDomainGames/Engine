package unknowndomain.engine;

import org.slf4j.Logger;
import unknowndomain.engine.client.EngineClient;

import java.util.Locale;

/**
 * Engine platform
 */
public class Platform {

    /**
     * Auto inject
     */
    private static Engine engine;

    public static Engine getEngine() {
        return engine;
    }

    /**
     * Get platform version
     */
    public static String getVersion() {
        return "0.0.1";
    }

    @Deprecated
    public static Locale getLocale() {
        return Locale.getDefault(); // TODO Game locale
    }

    public static boolean isClient() {
        return engine.isClient();
    }

    public static boolean isServer() {
        return engine.isServer();
    }

    public static Logger getLogger() {
        return engine.getLogger();
    }

    public static EngineClient getEngineClient() {
        if (isServer()) {
            throw new UnsupportedOperationException("Cannot get EngineClient on server.");
        }
        return (EngineClient) engine;
    }
}
