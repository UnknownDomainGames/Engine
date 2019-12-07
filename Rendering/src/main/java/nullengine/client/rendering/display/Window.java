package nullengine.client.rendering.display;

public interface Window extends WindowAttributes, WindowCallbacks {

    int getX();

    int getY();

    void setPos(int x, int y);

    int getWidth();

    int getHeight();

    Monitor getMonitor();

    void setMonitor(Monitor monitor);

    float getContentScaleX();

    float getContentScaleY();

    void setSize(int width, int height);

    boolean isResized();

    String getTitle();

    void setTitle(String title);

    void setWindowIcon(WindowIcons icons);

    Cursor getCursor();

    void close();

    boolean isClosed();

    void show();

    void hide();

    boolean isShowing();

    void dispose();

    void swapBuffers();

    DisplayMode getDisplayMode();

    default void setDisplayMode(DisplayMode mode) {
        setDisplayMode(mode, -1, -1, -1);
    }

    void setDisplayMode(DisplayMode mode, int newWidth, int newHeight, int frameRate);
}
