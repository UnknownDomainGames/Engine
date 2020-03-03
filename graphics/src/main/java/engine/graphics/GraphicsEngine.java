package engine.graphics;

import engine.graphics.image.ImageLoader;
import engine.graphics.lwjgl.font.WindowsFontHelper;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.management.GraphicsBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public final class GraphicsEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger("Graphics");

    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("graphics.debug", "false"));

    private static Settings settings;
    private static GraphicsBackend graphicsBackend;

    public static GraphicsBackend getGraphicsBackend() {
        return graphicsBackend;
    }

    public static boolean isDebug() {
        return settings.debug;
    }

    public static void start(Settings settings) {
        if (graphicsBackend != null) {
            throw new IllegalArgumentException("Graphics engine has been started.");
        }
        GraphicsEngine.settings = settings;
        ImageLoader.initialize("stb");
        graphicsBackend = ServiceLoader.load(GraphicsBackendFactory.class)
                .stream()
                .filter(provider -> provider.get().getName().equals(settings.backend))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No found graphics backend: " + settings.backend))
                .get()
                .create();
        LOGGER.info("Graphics backend: {}", settings.backend);
        graphicsBackend.init();
        // TODO: create font helper by factory
        WindowsFontHelper.initialize();
    }

    public static void doRender(float tpf) {
        graphicsBackend.render(tpf);
    }

    public static void stop() {
        if (graphicsBackend == null) {
            return;
        }
        graphicsBackend.dispose();
        graphicsBackend = null;
    }

    public static class Settings {
        private boolean debug = DEBUG;
        private String backend = "opengl";
        private String imageio = "stb";

        public Settings debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Settings backend(String backend) {
            this.backend = backend;
            return this;
        }

        public Settings imageio(String imageio) {
            this.imageio = imageio;
            return this;
        }
    }

    private GraphicsEngine() {
    }
}
