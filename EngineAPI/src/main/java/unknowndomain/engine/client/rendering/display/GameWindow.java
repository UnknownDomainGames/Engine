package unknowndomain.engine.client.rendering.display;

import org.joml.Matrix4f;

public interface GameWindow {

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    Matrix4f projection();

    boolean isResized();

    String getTitle();

    void setTitle(String title);

    void showCursor();

    void disableCursor();

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
