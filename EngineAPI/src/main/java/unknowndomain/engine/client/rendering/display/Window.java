package unknowndomain.engine.client.rendering.display;

import org.joml.Matrix4fc;

public interface Window {

    int getWidth();

    int getHeight();

    float getContentScaleX();

    float getContentScaleY();

    void setSize(int width, int height);

    Matrix4fc projection();

    boolean isResized();

    String getTitle();

    void setTitle(String title);

    Cursor getCursor();

    void close();

    boolean isClosed();

    void show();

    void hide();

    void setVisible(boolean visable);

    boolean isVisible();

    void addKeyCallback(KeyCallback callback);

    void removeKeyCallback(KeyCallback callback);

    void addMouseCallback(MouseCallback callback);

    void removeMouseCallback(MouseCallback callback);

    void addCursorCallback(CursorCallback callback);

    void removeCursorCallback(CursorCallback callback);

    void addScrollCallback(ScrollCallback callback);

    void removeScrollCallback(ScrollCallback callback);

    void addCharCallback(CharCallback callback);

    void removeCharCallback(CharCallback callback);

    void beginRender();

    void endRender();

    int getFps();

    @FunctionalInterface
    interface KeyCallback {
        void invoke(int key, int scancode, int action, int mods);
    }

    @FunctionalInterface
    interface MouseCallback {
        void invoke(int button, int action, int mods);
    }

    @FunctionalInterface
    interface CursorCallback {
        void invoke(double xpos, double ypos);
    }

    @FunctionalInterface
    interface ScrollCallback {
        void invoke(double xoffset, double yoffset);
    }

    @FunctionalInterface
    interface CharCallback {
        void invoke(char c);
    }
}
