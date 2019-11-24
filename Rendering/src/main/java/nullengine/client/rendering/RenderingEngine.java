package nullengine.client.rendering;

import nullengine.client.rendering.gl.GLRenderingContext;
import nullengine.client.rendering.management.RenderingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RenderingEngine {
    public static final Logger LOGGER = LoggerFactory.getLogger("Rendering");

    private static RunOptions runOptions;

    // TODO: delegate render context creation
    private static GLRenderingContext context;

    public static void start(RenderingListener listener) {
        start(new RunOptions(), listener);
    }

    public static void start(RunOptions runOptions, RenderingListener listener) {
        if (context != null) {
            throw new IllegalArgumentException("Rendering engine has been started.");
        }
        RenderingEngine.runOptions = runOptions;
        context = new GLRenderingContext(listener);
        context.init();
    }

    public static void doRender(float partial) {
        context.render(partial);
    }

    public static void stop() {
        if (context == null) {
            return;
        }
        context.dispose();
        context = null;
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

    private RenderingEngine() {
    }
}
