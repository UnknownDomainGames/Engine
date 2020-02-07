package nullengine.client.hud;

import java.util.Map;

public interface HUDManager {
    /**
     * @see #setVisible(boolean)
     */
    void toggleVisible();

    void setVisible(boolean visible);

    boolean isVisible();

    void add(HUDControl control);

    void remove(String name);

    void remove(HUDControl control);

    Map<String, HUDControl> getControls();
}
