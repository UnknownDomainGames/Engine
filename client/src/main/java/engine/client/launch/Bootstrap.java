package engine.client.launch;

import engine.Engine;
import engine.Platform;
import engine.client.EngineClientImpl;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.Path;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());

        Engine engine = new EngineClientImpl(Path.of("run"));
        injectEngine(engine);
        engine.initEngine();
        engine.runEngine();
    }

    private static void injectEngine(Engine engine) {
        try {
            Field engineField = Platform.class.getDeclaredField("engine");
            engineField.setAccessible(true);
            engineField.set(null, engine);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot initialize engine");
        }
    }
}
