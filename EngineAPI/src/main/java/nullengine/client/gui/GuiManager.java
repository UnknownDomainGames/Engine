package nullengine.client.gui;

import java.util.Map;

public interface GuiManager {

    void showScreen(Scene scene);

    void showLastScreen();

    void closeScreen();

    Scene getDisplayingScreen();

    boolean isDisplayingScreen();

    void showHud(String id, Scene hud);

    void showHud(String id);

    void showHuds();

    void hideHud(String id);

    void hideHuds();

    void removeHud(String id);

    void clearHuds();

    Map<String, Scene> getHuds();
}
