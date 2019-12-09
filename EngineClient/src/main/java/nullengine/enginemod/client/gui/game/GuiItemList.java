package nullengine.enginemod.client.gui.game;

import nullengine.client.gui.component.ItemView;
import nullengine.client.gui.event.old.MouseEvent_;
import nullengine.client.gui.layout.AnchorPane;
import nullengine.client.gui.layout.HBox;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.text.Text;
import nullengine.client.rendering.RenderManager;
import nullengine.entity.component.TwoHands;
import nullengine.item.Item;
import nullengine.item.ItemStack;
import nullengine.registry.Registries;
import nullengine.util.Color;

import java.util.Map;

public class GuiItemList extends AnchorPane {
    public GuiItemList(RenderManager context) {
        var vBox = new VBox();
        vBox.background().setValue(Background.fromColor(Color.fromARGB(0x7f000000)));
        vBox.spacing().set(5f);
        AnchorPane.setBottomAnchor(vBox, 50f);
        AnchorPane.setTopAnchor(vBox, 50f);
        AnchorPane.setLeftAnchor(vBox, 50f);
        AnchorPane.setRightAnchor(vBox, 50f);
        var text = new Text("Registered Item List");
        vBox.getChildren().add(text);
        var hBox = new HBox();
        for (Map.Entry<String, Item> entry : Registries.getItemRegistry().getEntries()) {
            ItemView view = new ItemView(new ItemStack(entry.getValue())) {
                @Override
                public void onClick(MouseEvent_.MouseClickEvent event) {
                    context.getEngine().getCurrentGame().getClientPlayer().getControlledEntity()
                            .getComponent(TwoHands.class)
                            .ifPresent(twoHands -> twoHands.setMainHand(item().getValue()));
                }
            };
            view.viewSize().set(40);
            hBox.getChildren().add(view);
        }
        vBox.getChildren().add(hBox);
        this.getChildren().add(vBox);
    }
}
