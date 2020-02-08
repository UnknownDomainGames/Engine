package engine.graphics;

import engine.graphics.gl.GLRenderManager;
import engine.graphics.lwjgl.STBImageHelper;
import engine.graphics.management.RenderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RenderEngine {
    public static final Logger LOGGER = LoggerFactory.getLogger("Rendering");

    private static Settings settings;
    private static RenderManager manager;

    public static RenderManager getManager() {
        return manager;
    }

    public static boolean isDebug() {
        return settings.debug;
    }

    public static void start(Settings settings) {
        if (manager != null) {
            throw new IllegalArgumentException("Rendering engine has been started.");
        }
        RenderEngine.settings = settings;
        // TODO: delegate render context creation
        STBImageHelper.init();
        manager = new GLRenderManager();
        manager.init();
    }

    public static void doRender(float tpf) {
        manager.render(tpf);
    }

    public static void stop() {
        if (manager == null) {
            return;
        }
        manager.dispose();
        manager = null;
    }

    public static class Settings {
        private boolean debug = Boolean.parseBoolean(System.getProperty("rendering.debug", "false"));

        public Settings debug(boolean debug) {
            this.debug = debug;
            return this;
        }
    }

    private RenderEngine() {
    }
}
