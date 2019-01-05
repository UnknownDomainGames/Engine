package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.rendering.display.GameWindow;

import java.util.List;
import java.util.Optional;

public interface GUIContext {

    GameWindow getWindow();

    Optional<Scene> getGui();

    void setGui(Scene scene);

    List<Scene> getHUDs();
}
