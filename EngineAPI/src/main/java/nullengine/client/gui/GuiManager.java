package nullengine.client.gui;

import java.util.Map;

public interface GuiManager {
    void showScreen(Scene scene);

    void showLastScreen();

    void showHud(String id, Scene hud);

    void closeScreen();

    void hideHud(String id);

    Map<String, Scene> getHuds();

    Scene getDisplayingScreen();

    boolean isDisplayingScreen();
}
