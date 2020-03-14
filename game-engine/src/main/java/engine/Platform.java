package engine;

import engine.client.EngineClient;
import engine.util.RuntimeEnvironment;
import org.slf4j.Logger;

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
        return "0.2.0.1-rc";
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

    public static RuntimeEnvironment getRuntimeEnvironment() {
        return engine.getRuntimeEnvironment();
    }
}
