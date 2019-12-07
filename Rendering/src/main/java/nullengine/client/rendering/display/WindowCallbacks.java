package nullengine.client.rendering.display;

import nullengine.client.rendering.display.callback.*;

public interface WindowCallbacks {
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

    void addDropCallback(DropCallback callback);

    void removeDropCallback(DropCallback callback);
}
