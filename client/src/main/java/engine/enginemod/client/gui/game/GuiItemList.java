package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.entity.component.TwoHands;
import engine.graphics.GraphicsManager;
import engine.gui.Scene;
import engine.gui.control.ItemView;
import engine.gui.control.Text;
import engine.gui.layout.AnchorPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.input.KeyCode;
import engine.item.Item;
import engine.item.ItemStack;
import engine.registry.Registries;
import engine.server.network.packet.c2s.PacketTwoHandComponentChange;
import engine.util.Color;

import java.util.Map;

public final class GuiItemList extends AnchorPane {
    public static Scene create() {
        Scene scene = new Scene(new GuiItemList());
        scene.setOnKeyPressed(event -> {
            if (event.getKey() == KeyCode.ESCAPE) {
                GraphicsManager.instance().getGUIManager().close();
            }
        });
        return scene;
    }

    public GuiItemList() {
        var vBox = new VBox();
        vBox.setBackground(Background.fromColor(Color.fromARGB(0x7f000000)));
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
            itemView.setOnMouseClicked(event -> {
                var player = Platform.getEngineClient().getCurrentClientGame().getClientPlayer();
                player.getControlledEntity()
                        .getComponent(TwoHands.class)
                        .ifPresent(twoHands -> {
                            twoHands.setMainHand(itemView.getItemStack());
                            player.getNetworkHandler().sendPacket(new PacketTwoHandComponentChange.Builder().setMainHand(itemView.getItemStack()).build());
                        });
            });
            itemView.size().set(40);
            hBox.getChildren().add(itemView);
        }
        vBox.getChildren().add(hBox);
        this.getChildren().add(vBox);
    }
}
