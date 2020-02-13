package engine.enginemod.client.gui.hud;

import engine.Platform;
import engine.client.event.rendering.RenderEvent;
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
        super("HandingItem");
        AnchorPane.setLeftAnchor(this, 0f);
        AnchorPane.setBottomAnchor(this, 0f);
        mainHandItem = new ItemView();
        mainHandItem.viewSize().set(40);
        setContent(mainHandItem);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        if (visible) Platform.getEngineClient().getEventBus().register(this);
        else Platform.getEngineClient().getEventBus().unregister(this);
    }

    @Listener
    public void update(RenderEvent.Pre event) {
        Player player = Platform.getEngineClient().getCurrentGame().getClientPlayer();
        if (player != null) {
            Entity entity = player.getControlledEntity();
            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                    mainHandItem.item().setValue(twoHands.getMainHand()));
        }
    }
}
