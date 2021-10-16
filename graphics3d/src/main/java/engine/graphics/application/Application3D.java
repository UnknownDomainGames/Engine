package engine.graphics.application;

import engine.graphics.GraphicsEngine;
import engine.graphics.Scene3D;
import engine.graphics.backend.GraphicsBackend;
import engine.graphics.util.FrameTicker;
import engine.graphics.viewport.PerspectiveViewport;

public abstract class Application3D {

    protected final FrameTicker ticker = new FrameTicker(this::doRender);

    protected GraphicsBackend manager;

    protected PerspectiveViewport mainViewPort;
    protected Scene3D mainScene;

    @SuppressWarnings("unchecked")
    public static void launch(String[] args) {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackElements.length; i++) {
            StackTraceElement element = stackElements[i];
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                if (Application3D.class.isAssignableFrom(clazz)) {
                    launch((Class<? extends Application3D>) clazz, args);
                    return;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new RuntimeException("Cannot launch application, application class not found.");
    }

    public static void launch(Class<? extends Application3D> clazz, String[] args) {
        try {
            GraphicsEngine.start(new GraphicsEngine.Settings());
            Application3D application = clazz.getConstructor().newInstance();
            application.doInitialize();
        } catch (Exception e) {
            throw new RuntimeException("Cannot launch application " + clazz, e);
        }

    }

    protected void onInitialized() throws Exception {
    }

    protected void onStopping() {
    }

    protected void onStopped() {
    }

    protected void onPreRender() {
    }

    protected void onPostRender() {
    }

    private void doInitialize() throws Exception {
        manager = GraphicsEngine.getGraphicsBackend();
        mainViewPort = new PerspectiveViewport();
        mainScene = new Scene3D();
        mainViewPort.setScene(mainScene);
        mainViewPort.show().bindWindow(manager.getPrimaryWindow());
        onInitialized();
        ticker.run();
    }

    private void doRender() {
        onPreRender();
        GraphicsEngine.doRender(0);
        onPostRender();
    }

    public synchronized final void stop() {
        onStopping();
        ticker.stop();
        GraphicsEngine.stop();
        onStopped();
    }
}
