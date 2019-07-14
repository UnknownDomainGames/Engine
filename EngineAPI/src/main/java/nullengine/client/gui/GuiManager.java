package nullengine.client.gui;

import java.util.Map;

public interface GuiManager {

    void showScreen(Scene scene);

    void showLastScreen();

    void closeScreen();

    Scene getDisplayingScreen();

    boolean isDisplayingScreen();

    /**
     * @see GuiManager#setHudVisible(boolean)
     */
    void toggleHudVisible();

    void setHudVisible(boolean visible);

    boolean isHudVisible();

    void showHud(String id, Scene hud);

    void removeHud(String id);

    void showHud(String id);

    void hideHud(String id);

    void clearHuds();

    Map<String, Scene> getHuds();

    Map<String, Scene> getDisplayingHuds();
}
