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

    void addCharModsCallback(CharModsCallback callback);

    void removeCharModsCallback(CharModsCallback callback);

    void addWindowCloseCallback(WindowCloseCallback callback);

    void removeWindowCloseCallback(WindowCloseCallback callback);

    void addWindowFocusCallback(WindowFocusCallback callback);

    void removeWindowFocusCallback(WindowFocusCallback callback);

    void addWindowIconifyCallback(WindowIconifyCallback callback);

    void removeWindowIconifyCallback(WindowIconifyCallback callback);

    void addWindowMaximizeCallback(WindowMaximizeCallback callback);

    void removeWindowMaximizeCallback(WindowMaximizeCallback callback);

    void addCursorEnterCallback(CursorEnterCallback callback);

    void removeCursorEnterCallback(CursorEnterCallback callback);

    void addWindowSizeCallback(WindowSizeCallback callback);

    void removeWindowSizeCallback(WindowSizeCallback callback);

    void addWindowPosCallback(WindowPosCallback callback);

    void removeWindowPosCallback(WindowPosCallback callback);

    void addDropCallback(DropCallback callback);

    void removeDropCallback(DropCallback callback);
}
