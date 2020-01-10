package nullengine.client.rendering.application;

import nullengine.client.rendering.RenderEngine;
import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering.scene.PerspectiveViewPort;
import nullengine.client.rendering.scene.Scene3D;
import nullengine.client.rendering.util.FrameTicker;

public abstract class RenderableApplication {

    protected final FrameTicker ticker = new FrameTicker(this::doRender);

    protected RenderManager manager;

    protected PerspectiveViewPort mainViewPort;
    protected Scene3D mainScene;

    public static void launch(String[] args) {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackElements.length; i++) {
            StackTraceElement element = stackElements[i];
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                if (RenderableApplication.class.isAssignableFrom(clazz)) {
                    launch((Class<? extends RenderableApplication>) clazz, args);
                    return;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new RuntimeException("Cannot launch application, application class not found.");
    }

    public static void launch(Class<? extends RenderableApplication> clazz, String[] args) {
        try {
            RenderableApplication application = clazz.getConstructor().newInstance();
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
        RenderEngine.start(new RenderEngine.Settings());
        manager = RenderEngine.getManager();
        mainViewPort = new PerspectiveViewPort();
        mainViewPort.setClearMask(true, true, true);
        mainViewPort.bindWindow(manager.getPrimaryWindow());
        mainScene = new Scene3D();
        mainViewPort.setScene(mainScene);
//        manager.setPrimaryViewPort(mainViewPort);
        onInitialized();
        ticker.run();
    }

    private void doRender() {
        onPreRender();
        RenderEngine.doRender(ticker.getTpf());
        onPostRender();
    }

    public synchronized final void stop() {
        onStopping();
        ticker.stop();
        RenderEngine.stop();
        onStopped();
    }
}
