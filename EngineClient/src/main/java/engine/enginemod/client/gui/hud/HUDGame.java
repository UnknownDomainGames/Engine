package engine.enginemod.client.gui.hud;

import engine.Platform;
import engine.client.event.rendering.RenderEvent;
import engine.gui.control.ItemView;
import engine.gui.layout.AnchorPane;
import engine.entity.Entity;
import engine.entity.component.TwoHands;
import engine.event.Listener;
import engine.player.Player;

public class HUDGame extends AnchorPane {

    private ItemView mainhandItem;

    public HUDGame() {
        mainhandItem = new ItemView();
        mainhandItem.viewSize().set(40);
        AnchorPane.setLeftAnchor(mainhandItem, 0f);
        AnchorPane.setBottomAnchor(mainhandItem, 0f);
        this.getChildren().add(mainhandItem);
    }

    @Listener
    public void update(RenderEvent.Pre event) {
        Player player = Platform.getEngineClient().getCurrentGame().getClientPlayer();
        if (player != null) {
            Entity entity = player.getControlledEntity();
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    mainhandItem.item().setValue(twoHands.getMainHand()));
        }
    }
}
