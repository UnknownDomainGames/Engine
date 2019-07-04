package nullengine.client.launch;

import nullengine.Engine;
import nullengine.Platform;
import nullengine.client.EngineClientImpl;
import nullengine.exception.UninitializationException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

public class Bootstrap {

    public static void main(String[] args) {
        switch (Platform.getRunningOsPlatform()) {// TODO: require review: where should we put this OS checking
            case MACOSX:
                System.setProperty("java.awt.headless", "true");
                break;
            case WINDOWS:
                if (User32.Functions.SetThreadDpiAwarenessContext != MemoryUtil.NULL) {
                    User32.SetThreadDpiAwarenessContext(User32.IsValidDpiAwarenessContext(User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2) ? User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2 : User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE);
                }
                break;
            case LINUX:

                break;
        }

        System.out.println(ManagementFactory.getRuntimeMXBean().getName());

        Engine engine = new EngineClientImpl();
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
            throw new UninitializationException("Cannot initialize engine");
        }
    }
}
