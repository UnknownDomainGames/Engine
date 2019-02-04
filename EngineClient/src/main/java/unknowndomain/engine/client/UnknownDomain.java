package unknowndomain.engine.client;

import org.apache.commons.lang3.SystemUtils;
import unknowndomain.engine.Engine;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.game.GameClientStandalone;

import java.lang.reflect.Field;

public class UnknownDomain {

    private static final String NAME = "UnknownDomain";
    private static final String VERSION = "0.0.1";

    private static EngineClient engine;

    public static void main(String[] args) {
        if (SystemUtils.IS_OS_MAC) { // TODO: require review: where should we
            // put this OS checking
            System.setProperty("java.awt.headless", "true");
        }

        engine = new EngineClient();
        injectEngine(engine);
        engine.initEngine();
    }

    public static EngineClient getEngine() {
        return engine;
    }

    public static GameClientStandalone getGame() {
        return engine.getCurrentGame();
    }

    public static String getName() {
        return NAME;
    }

    public static String getVersion() {
        return VERSION;
    }

    private static void injectEngine(Engine engine) {
        try {
            Field engineField = Platform.class.getDeclaredField("engine");
            engineField.setAccessible(true);
            engineField.set(null, engine);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
