package nullengine.client.rendering;

import nullengine.client.rendering.gl.GLRenderManager;
import nullengine.client.rendering.management.RenderListener;
import nullengine.client.rendering.management.RenderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RenderEngine {
    public static final Logger LOGGER = LoggerFactory.getLogger("Rendering");

    private static RunOptions runOptions;

    // TODO: delegate render context creation
    private static RenderManager manager;

    public static RenderManager getManager() {
        return manager;
    }

    public static void start(RenderListener listener) {
        start(new RunOptions(), listener);
    }

    public static void start(RunOptions runOptions, RenderListener listener) {
        if (manager != null) {
            throw new IllegalArgumentException("Rendering engine has been started.");
        }
        RenderEngine.runOptions = runOptions;
        manager = new GLRenderManager(listener);
        manager.init();
    }

    public static void doRender(float partial) {
        manager.render(partial);
    }

    public static void stop() {
        if (manager == null) {
            return;
        }
        manager.dispose();
        manager = null;
    }

    public static class RunOptions {
        private boolean debug = Boolean.parseBoolean(System.getProperty("rendering.debug", "false"));

        public boolean isDebug() {
            return debug;
        }

        public RunOptions setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }
    }

    private RenderEngine() {
    }
}
