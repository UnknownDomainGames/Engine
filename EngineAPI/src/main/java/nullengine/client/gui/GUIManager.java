package nullengine.client.gui;

public interface GUIManager {

    boolean isShowing();

    void show(Scene scene);

    void showLast();

    void close();

    Scene getShowingScene();
}
