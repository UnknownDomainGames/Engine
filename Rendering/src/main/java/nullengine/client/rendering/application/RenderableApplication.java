package nullengine.client.rendering.application;

import nullengine.client.rendering.RenderEngine;

public abstract class RenderableApplication {

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
    }

    public static void launch(Class<? extends RenderableApplication> clazz, String[] args) {
        try {
            RenderableApplication application = clazz.getConstructor().newInstance();
            RenderEngine.start(new RenderEngine.Settings());
            application.onStarted();
        } catch (Exception e) {
            throw new RuntimeException("Cannot launch renderable application " + clazz, e);
        }
    }

    protected void onStarted() {

    }

    protected void onStopping() {

    }

    protected void onStopped() {

    }

    public synchronized final void stop() {
        onStopping();

        RenderEngine.stop();

        onStopped();
    }
}
