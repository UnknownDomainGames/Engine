package engine.gui;

import engine.client.hud.HUDControl;
import engine.client.hud.HUDManager;
import engine.gui.layout.AnchorPane;

import java.util.HashMap;
import java.util.Map;

public final class EngineHUDManager implements HUDManager {

    private final AnchorPane hudPane;

    private Map<String, HUDControl> hudControls = new HashMap<>();

    public EngineHUDManager(Stage stage) {
        this.hudPane = new AnchorPane();
        stage.setScene(new Scene(hudPane));
    }

    @Override
    public void toggleVisible() {
        setVisible(!isVisible());
    }

    @Override
    public void setVisible(boolean visible) {
        hudPane.setVisible(visible);
        hudControls.values().stream().filter(Node::isVisible).forEach(hudControl ->
                hudControl.onVisibleChanged(visible));
    }

    @Override
    public boolean isVisible() {
        return hudPane.isVisible();
    }

    @Override
    public void add(HUDControl control) {
        if (hudControls.containsKey(control.getName())) {
            throw new IllegalStateException("HUD has exists");
        }
        hudControls.put(control.getName(), control);
        hudPane.getChildren().add(control);
    }

    @Override
    public void remove(String name) {
        remove(hudControls.get(name));
    }

    @Override
    public void remove(HUDControl control) {
        if (control == null) return;
        hudControls.remove(control.getName());
        hudPane.getChildren().remove(control);
    }

    @Override
    public Map<String, HUDControl> getControls() {
        return hudControls;
    }
}
