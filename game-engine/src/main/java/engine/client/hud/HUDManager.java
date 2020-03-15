package engine.client.hud;

import java.util.Collection;

public interface HUDManager {

    float getScaleX();

    float getScaleY();

    void setScale(float scaleX, float scaleY);

    /**
     * @see #setVisible(boolean)
     */
    void toggleVisible();

    void setVisible(boolean visible);

    boolean isVisible();

    HUDControl getControl(String name);

    Collection<HUDControl> getControls();
}
