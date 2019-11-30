package nullengine.client.rendering.display;

import nullengine.client.rendering.display.callback.*;

public interface Window extends WindowAttributes {

    int getX();

    int getY();

    void setPos(int x, int y);

    int getWidth();

    int getHeight();

    int getFrameBufferWidth();

    int getFrameBufferHeight();

    Monitor getMonitor();

    void setMonitor(Monitor monitor);

    float getContentScaleX();

    float getContentScaleY();

    void setSize(int width, int height);

    boolean isResized();

    String getTitle();

    void setTitle(String title);

    Cursor getCursor();

    void close();

    boolean isClosed();

    void show();

    void hide();

    boolean isShowing();

    void dispose();

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

    void addWindowCloseCallback(WindowCloseCallback callback);

    void removeWindowCloseCallback(WindowCloseCallback callback);

    void addWindowFocusCallback(WindowFocusCallback callback);

    void removeWindowFocusCallback(WindowFocusCallback callback);

    void addCursorEnterCallback(CursorEnterCallback callback);

    void removeCursorEnterCallback(CursorEnterCallback callback);

    void addFramebufferSizeCallback(FramebufferSizeCallback callback);

    void removeFramebufferSizeCallback(FramebufferSizeCallback callback);

    void addWindowPosCallback(WindowPosCallback callback);

    void removeWindowPosCallback(WindowPosCallback callback);

    void swapBuffers();

    DisplayMode getDisplayMode();

    default void setDisplayMode(DisplayMode mode) {
        setDisplayMode(mode, -1, -1, -1);
    }

    void setDisplayMode(DisplayMode mode, int newWidth, int newHeight, int frameRate);
}
