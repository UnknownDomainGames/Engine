package engine.enginemod.client.gui.hud;

import engine.Platform;
import engine.client.event.graphics.RenderEvent;
import engine.client.hud.HUDControl;
import engine.entity.Entity;
import engine.entity.component.TwoHands;
import engine.event.Listener;
import engine.gui.control.ItemView;
import engine.gui.layout.AnchorPane;
import engine.player.Player;

public final class HUDHandingItem extends HUDControl {

    private final ItemView mainHandItem;

    public HUDHandingItem() {
        name("handing_item");
        AnchorPane.setLeftAnchor(this, 0D);
        AnchorPane.setBottomAnchor(this, 0D);
        mainHandItem = new ItemView();
        mainHandItem.size().set(40);
        setContent(mainHandItem);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        if (visible) Platform.getEngineClient().getEventBus().register(this);
        else Platform.getEngineClient().getEventBus().unregister(this);
    }

    @Listener
    public void update(RenderEvent.Pre event) {
        if (!Platform.getEngine().isPlaying()) return;
        Player player = Platform.getEngineClient().getCurrentClientGame().getClientPlayer();
        if (player != null) {
            Entity entity = player.getControlledEntity();
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    mainHandItem.itemStack().set(twoHands.getMainHand()));
        }
    }
}
