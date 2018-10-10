package unknowndomain.engine.client.display;

public interface GameWindow {

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    boolean isResized();

    String getTitle();

    void setTitle(String title);

    void showCursor();

    void disableCursor();
}
