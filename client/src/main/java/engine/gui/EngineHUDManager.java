package engine.gui;

import engine.Platform;
import engine.client.hud.HUDControl;
import engine.client.hud.HUDManager;
import engine.event.Listener;
import engine.event.game.GameStartEvent;
import engine.event.game.GameTerminationEvent;
import engine.gui.layout.AnchorPane;
import engine.gui.stage.Stage;
import engine.registry.Registries;
import engine.registry.Registry;

import java.util.Collection;
import java.util.List;

public final class EngineHUDManager implements HUDManager {

    private final AnchorPane hudPane;

    private Registry<HUDControl> hudControls;

    public EngineHUDManager(Stage stage) {
        this.hudPane = new AnchorPane();
        stage.setScene(new Scene(hudPane));
        Platform.getEngine().getEventBus().register(this);
    }

    @Override
    public void toggleVisible() {
        setVisible(!isVisible());
    }

    @Override
    public void setVisible(boolean visible) {
        hudPane.setVisible(visible);
        getControls().stream().filter(Node::isVisible).forEach(hudControl ->
                hudControl.onVisibleChanged(visible));
    }

    @Override
    public boolean isVisible() {
        return hudPane.isVisible();
    }

    @Override
    public HUDControl getControl(String name) {
        return hudControls == null ? null : hudControls.getValue(name);
    }

    @Override
    public Collection<HUDControl> getControls() {
        return hudControls == null ? List.of() : hudControls.getValues();
    }

    @Listener
    public void onGameReady(GameStartEvent.Post event) {
        Registries.getRegistryManager().getRegistry(HUDControl.class).ifPresent(registry -> {
            hudControls = registry;
            hudPane.getChildren().addAll(registry.getValues());
        });
    }

    @Listener
    public void onGameMarkedStop(GameTerminationEvent.Marked event) {
        hudPane.getChildren().clear();
        hudControls = null;
    }
}
