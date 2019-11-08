package nullengine.client.rendering.display;

public interface WindowAttributes {
    void setVisible(boolean visible);

    boolean isVisible();

    boolean isDecorated();

    void setDecorated(boolean decorated);

    boolean isResizable();

    void setResizable(boolean resizable);
}
