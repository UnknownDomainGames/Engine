package engine.graphics.display;

import engine.graphics.display.callback.*;
import engine.graphics.image.ReadOnlyImage;

public interface Window {

    Window getParent();

    int getX();

    int getY();

    void setPos(int x, int y);

    void centerOnScreen();

    int getWidth();

    int getHeight();

    Screen getScreen();

    void setScreen(Screen screen);

    float getContentScaleX();

    float getContentScaleY();

    void setSize(int width, int height);

    boolean isResized();

    DisplayMode getDisplayMode();

    default void setDisplayMode(DisplayMode mode) {
        setDisplayMode(mode, -1, -1, -1);
    }

    void setDisplayMode(DisplayMode mode, int width, int height, int frameRate);

    String getTitle();

    void setTitle(String title);

    void setIcon(ReadOnlyImage... icons);

    Cursor getCursor();

    void show();

    void hide();

    boolean isShowing();

    boolean isDoCloseImmediately();

    void setDoCloseImmediately(boolean doCloseImmediately);

    void prepareDraw();

    void swapBuffers();

    void dispose();

    // ================= Window Attributes Start =================
    boolean isFocused();

    void focus();

    boolean isDecorated();

    void setDecorated(boolean decorated);

    boolean isResizable();

    void setResizable(boolean resizable);

    boolean isFloating();

    void setFloating(boolean floating);

    boolean isTransparent();

    void setTransparent(boolean transparent);

    boolean isIconified();

    boolean isMaximized();

    void iconify();

    void maximize();

    void restore();
    // ================= Window Attributes End =================

    // ================= Window Callbacks Start =================
    void addKeyCallback(KeyCallback callback);

    void removeKeyCallback(KeyCallback callback);

    void addMouseCallback(MouseCallback callback);

    void removeMouseCallback(MouseCallback callback);

    void addCursorCallback(CursorCallback callback);

    void removeCursorCallback(CursorCallback callback);

    void addScrollCallback(ScrollCallback callback);

    void removeScrollCallback(ScrollCallback callback);

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
    // ================= Window Callbacks End =================
}
