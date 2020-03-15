package engine.gui;

public interface GUIManager {

    float getScaleX();

    float getScaleY();

    void setScale(float scaleX, float scaleY);

    boolean isShowing();

    void show(Scene scene);

    void showLast();

    void close();

    Scene getShowingScene();
}
