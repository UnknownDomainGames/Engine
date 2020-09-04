package engine.graphics;

import engine.graphics.animation.AnimationManager;
import engine.graphics.animation.RealtimeAnimationManager;
import engine.graphics.backend.GraphicsBackend;
import engine.graphics.backend.GraphicsBackendFactory;
import engine.graphics.font.FontManager;
import engine.graphics.image.ImageLoader;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public final class GraphicsEngine {

    public static final String DEBUG_PROPERTY = "graphics.debug";

    private static final Logger LOGGER = LoggerFactory.getLogger("Graphics");

    private static boolean debug = true;

    private static Settings settings;
    private static GraphicsBackend graphicsBackend;
    private static AnimationManager animationManager;

    public static GraphicsBackend getGraphicsBackend() {
        return graphicsBackend;
    }

    public static AnimationManager getAnimationManager() {
        return animationManager;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void start(Settings settings) {
        if (graphicsBackend != null) {
            throw new IllegalArgumentException("Graphics engine has been started.");
        }
        debug = Boolean.parseBoolean(System.getProperty(DEBUG_PROPERTY, "false"));
        if (debug) {
            LOGGER.info("Graphics debug enable!");
        }
        GraphicsEngine.settings = settings;
        initEnvironment();
        ImageLoader.initialize(settings.imageLoader);
        graphicsBackend = ServiceLoader.load(GraphicsBackendFactory.class)
                .stream()
                .filter(provider -> provider.get().getName().equals(settings.backend))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No found graphics backend: " + settings.backend))
                .get()
                .create();
        LOGGER.info("Graphics backend: {}", settings.backend);
        graphicsBackend.init();
        animationManager = new RealtimeAnimationManager();
        FontManager.initialize(settings.fontManager);
    }

    private static void initEnvironment() {
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("java.awt.headless", "true");
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            if (User32.Functions.SetThreadDpiAwarenessContext != MemoryUtil.NULL) {
                User32.SetThreadDpiAwarenessContext(User32.IsValidDpiAwarenessContext(User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2) ? User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2 : User32.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE);
            }
        }
    }

    public static void doRender(float timeToLastUpdate) {
        animationManager.update();
        graphicsBackend.render(timeToLastUpdate);
    }

    public static void stop() {
        if (graphicsBackend == null) {
            return;
        }
        graphicsBackend.dispose();
        graphicsBackend = null;
    }

    public static class Settings {
        private String backend = "opengl";
        private String imageLoader = "stb";
        private String fontManager = "stb";

        public Settings setGraphicsBackend(String backend) {
            this.backend = backend;
            return this;
        }

        public Settings setImageLoader(String imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public Settings setFontManager(String fontManager) {
            this.fontManager = fontManager;
            return this;
        }
    }

    private GraphicsEngine() {
    }
}
