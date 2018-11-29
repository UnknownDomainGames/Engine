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
}
