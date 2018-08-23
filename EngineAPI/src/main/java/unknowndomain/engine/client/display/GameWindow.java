package unknowndomain.engine.client.display;

public interface GameWindow {
    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    String getTitle();

    void setTitle(String title);
}
