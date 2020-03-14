package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.entity.component.TwoHands;
import engine.graphics.GraphicsManager;
import engine.gui.Scene;
import engine.gui.control.ItemView;
import engine.gui.layout.AnchorPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.text.Text;
import engine.input.KeyCode;
import engine.item.Item;
import engine.item.ItemStack;
import engine.registry.Registries;
import engine.util.Color;

import java.util.Map;

public final class GUIItemList extends AnchorPane {
    public static Scene create() {
        Scene scene = new Scene(new GUIItemList());
        scene.setOnKeyPressed(event -> {
            if (event.getKey() == KeyCode.ESCAPE) {
                GraphicsManager.instance().getGUIManager().close();
            }
        });
        return scene;
    }

    public GUIItemList() {
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
            ItemView itemView = new ItemView(new ItemStack(entry.getValue()));
            itemView.setOnMouseClicked(event -> Platform.getEngineClient().getCurrentGame().getClientPlayer().getControlledEntity()
                    .getComponent(TwoHands.class)
                    .ifPresent(twoHands -> twoHands.setMainHand(itemView.itemStack().get())));
            itemView.size().set(40);
            hBox.getChildren().add(itemView);
        }
        vBox.getChildren().add(hBox);
        this.getChildren().add(vBox);
    }
}
