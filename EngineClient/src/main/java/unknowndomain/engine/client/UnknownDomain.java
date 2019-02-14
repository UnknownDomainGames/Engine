package unknowndomain.engine.client;

import org.apache.commons.lang3.SystemUtils;
import unknowndomain.engine.Engine;
import unknowndomain.engine.Platform;

import java.lang.reflect.Field;

public class UnknownDomain {

    @Deprecated
    private static final String NAME = "UnknownDomain";

    public static void main(String[] args) {
        if (SystemUtils.IS_OS_MAC) { // TODO: require review: where should we
            // put this OS checking
            System.setProperty("java.awt.headless", "true");
        }

        Engine engine = new EngineClientImpl();
        injectEngine(engine);
        engine.initEngine();
        engine.runEngine();
    }

    @Deprecated
    public static String getName() {
        return NAME;
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
