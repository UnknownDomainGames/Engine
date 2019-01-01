package unknowndomain.engine.client;

import org.apache.commons.lang3.SystemUtils;
import unknowndomain.engine.client.game.GameClientStandalone;

public class UnknownDomain {

    private static final String NAME = "UnknownDomain";
    private static final String VERSION = "0.0.1";

    public static final int WIDTH = 854 + 1, HEIGHT = 480;//FIXME
    private static EngineClient engine;

    public static void main(String[] args) {
        if (SystemUtils.IS_OS_MAC && SystemUtils.JAVA_AWT_HEADLESS != null) { // TODO: require review: where should we
            // put this OS checking
            System.setProperty(SystemUtils.JAVA_AWT_HEADLESS, "true");
        }
        engine = new EngineClient(WIDTH, HEIGHT);
        engine.init();
        engine.startGame(null);
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
}
