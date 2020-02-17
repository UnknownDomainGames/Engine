package engine.graphics;

import engine.graphics.gl.GLGraphicsBackend;
import engine.graphics.lwjgl.STBImageHelper;
import engine.graphics.lwjgl.font.WindowsFontHelper;
import engine.graphics.management.GraphicsBackend;
import engine.graphics.vulkan.VKGraphicsBackend;

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
        if(settings.backend == Settings.Backend.VULKAN) {
            graphicsBackend = new VKGraphicsBackend();
        }
        else {
            graphicsBackend = new GLGraphicsBackend();
        }
        graphicsBackend.init();
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
        private Backend backend = Backend.GL;

        public Settings debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Settings backend(Backend backend) {
            this.backend = backend;
            return this;
        }

        public enum Backend {
            GL, VULKAN
        }
    }

    private GraphicsEngine() {
    }
}
