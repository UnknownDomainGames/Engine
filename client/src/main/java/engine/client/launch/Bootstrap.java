package engine.client.launch;

import engine.Engine;
import engine.Platform;
import engine.client.EngineClientImpl;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.Path;

public class Bootstrap {

    public static void main(String[] args) {
        // TODO: require review: where should we put this OS checking
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("java.awt.headless", "true");
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            if (User32.Functions.SetThreadDpiAwarenessContext != MemoryUtil.NULL) {
                User32.SetThreadDpiAwarenessContext(User32.IsValidDpiAwarenessContext(User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2) ? User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2 : User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE);
            }
        }

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
