package engine.graphics;

import engine.graphics.gl.GLGraphicsBackend;
import engine.graphics.lwjgl.STBImageHelper;
import engine.graphics.management.GraphicsBackend;

public final class GraphicsEngine {
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
        // TODO: delegate render context creation
        STBImageHelper.init();
        graphicsBackend = new GLGraphicsBackend();
        graphicsBackend.init();
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

        public Settings debug(boolean debug) {
            this.debug = debug;
            return this;
        }
    }

    private GraphicsEngine() {
    }
}
