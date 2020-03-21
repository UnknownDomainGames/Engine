package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Label;
import engine.gui.control.TextField;
import engine.gui.layout.FlowPane;
import engine.gui.layout.HBox;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Pos;
import engine.util.Color;

public class GuiDirectConnectServer extends FlowPane {
    public GuiDirectConnectServer(){
        alignment().set(Pos.CENTER);
        var vmain = new VBox();
        vmain.alignment().set(Pos.HPos.CENTER);
        var vbox = new VBox();
        vbox.alignment().set(Pos.HPos.CENTER);
        vmain.getChildren().add(vbox);
        getChildren().add(vmain);
        var label1 = new Label("Connect to server");

        var addressFieldGroup = new VBox();
        var lblAddress = new Label("Address");
        var txtboxAddress = new TextField();
        txtboxAddress.getSize().prefHeight().set(23.0f);
        txtboxAddress.getSize().prefWidth().set(200f);
        addressFieldGroup.getChildren().addAll(lblAddress, txtboxAddress);
        var hbox = new HBox();
        hbox.spacing().set(10f);
        var butConnect = new Button("Connect");
        butConnect.setOnMouseClicked(e -> {
            var fullAddress = txtboxAddress.text().get();
            var port = 18104;
            var colonIndex = fullAddress.lastIndexOf(":");
            if (colonIndex != -1) {
                try {
                    port = Integer.parseInt(fullAddress.substring(colonIndex + 1));
                } catch (NumberFormatException ex) {

                }
                fullAddress = fullAddress.substring(0, colonIndex);
            }
            Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiServerConnectingStatus(fullAddress, port)));
        });
        var butBack = new Button("Back");
        butBack.setOnMouseClicked(e -> {
            var guiManager = Platform.getEngineClient().getGraphicsManager().getGUIManager();
            guiManager.showLast();
        });
        hbox.getChildren().addAll(butConnect, butBack);
        vbox.getChildren().addAll(label1, addressFieldGroup, hbox);
        setBackground(Background.fromColor(Color.fromRGB(0x7f7f7f)));
    }
}
