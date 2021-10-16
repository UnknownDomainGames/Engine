package engine.gui.application;

import engine.gui.internal.impl.GUIPlatformImpl;
import engine.gui.stage.Stage;

public abstract class GUIApplication {

    @SuppressWarnings("unchecked")
    public static void launch(String[] args) {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackElements.length; i++) {
            StackTraceElement element = stackElements[i];
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                if (GUIApplication.class.isAssignableFrom(clazz)) {
                    launch((Class<? extends GUIApplication>) clazz, args);
                    return;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new RuntimeException("Cannot launch application, application class not found.");
    }

    public static void launch(Class<? extends GUIApplication> clazz, String[] args) {
        try {
            GUIPlatformImpl.launch(clazz, args);
        } catch (Exception e) {
            throw new RuntimeException("Cannot launch application " + clazz, e);
        }
    }

    public abstract void start(Stage primaryStage) throws Exception;
}
