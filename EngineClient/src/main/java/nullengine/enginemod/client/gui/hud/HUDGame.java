package nullengine.enginemod.client.gui.hud;

import nullengine.Platform;
import nullengine.client.event.rendering.RenderEvent;
import nullengine.client.gui.component.ItemView;
import nullengine.client.gui.layout.AnchorPane;
import nullengine.entity.Entity;
import nullengine.entity.component.TwoHands;
import nullengine.event.Listener;
import nullengine.player.Player;

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
