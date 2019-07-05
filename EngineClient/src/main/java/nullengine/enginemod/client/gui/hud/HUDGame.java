package nullengine.enginemod.client.gui.hud;

import nullengine.client.gui.GuiTickable;
import nullengine.client.gui.component.ItemView;
import nullengine.client.gui.layout.AnchorPane;
import nullengine.client.rendering.RenderContext;
import nullengine.entity.Entity;
import nullengine.entity.component.TwoHands;

public class HUDGame extends AnchorPane implements GuiTickable {

    private ItemView mainhandItem;

    public HUDGame(){
        mainhandItem = new ItemView();
        mainhandItem.viewSize().set(40);
        AnchorPane.setLeftAnchor(mainhandItem,0f);
        AnchorPane.setBottomAnchor(mainhandItem,0f);
        this.getChildren().add(mainhandItem);
    }

    public void update(RenderContext context){
        Entity player = context.getEngine().getCurrentGame().getPlayer().getControlledEntity();
        player.getComponent(TwoHands.class).ifPresent(twoHands -> mainhandItem.item().setValue(twoHands.getMainHand()));
    }
}
