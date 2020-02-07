package nullengine.enginemod.client.gui.game;

import nullengine.Platform;
import nullengine.client.gui.Scene;
import nullengine.client.gui.control.Button;
import nullengine.client.gui.control.Label;
import nullengine.client.gui.control.TextField;
import nullengine.client.gui.layout.BorderPane;
import nullengine.client.gui.layout.HBox;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Pos;
import nullengine.util.Color;

public class GuiDirectConnectServer extends BorderPane {
    public GuiDirectConnectServer(){
        var vmain = new VBox();
        vmain.alignment().setValue(Pos.HPos.CENTER);
        var vbox = new VBox();
        vmain.getChildren().add(vbox);
        setAlignment(vmain, Pos.CENTER);
        center().setValue(vmain);
        var label1 = new Label();
        label1.text().setValue("Connect to server");
        var lblAddress = new Label();
        lblAddress.text().setValue("Address");
        var txtboxAddress = new TextField();
        txtboxAddress.getSize().prefHeight().set(23.0f);
        txtboxAddress.getSize().prefWidth().set(200f);
        var hbox = new HBox();
        hbox.spacing().set(10f);
        var butConnect = new Button("Connect");
        butConnect.setOnMouseClicked(e -> {
            var fullAddress = txtboxAddress.text().getValue();
            var port = 18104;
            var colonIndex = fullAddress.lastIndexOf(":");
            if (colonIndex != -1) {
                try {
                    port = Integer.parseInt(fullAddress.substring(colonIndex + 1));
                } catch (NumberFormatException ex) {

                }
                fullAddress = fullAddress.substring(0, colonIndex);
            }
            Platform.getEngineClient().getRenderManager().getGUIManager().show(new Scene(new GuiConnectServer(fullAddress, port)));
        });
        var butBack = new Button("Back");
        butBack.setOnMouseClicked(e -> {
            var guiManager = Platform.getEngineClient().getRenderManager().getGUIManager();
            guiManager.showLast();
        });
        hbox.getChildren().addAll(butConnect, butBack);
        vbox.getChildren().addAll(label1, lblAddress, txtboxAddress, hbox);
        background().setValue(Background.fromColor(Color.fromRGB(0x7f7f7f)));
    }
}
