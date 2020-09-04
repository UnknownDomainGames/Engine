package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.client.i18n.I18n;
import engine.enginemod.client.gui.GuiSettings;
import engine.graphics.font.Font;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Text;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.misc.Pos;
import engine.util.Color;

import java.nio.file.Path;

public class GUIMainMenu extends FlowPane {

    public GUIMainMenu() {
        alignment().set(Pos.CENTER);
        setBackground(new Background(Color.fromRGB(0xAAAAAA)));

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setFillWidth(true);
        getChildren().add(vBox);

        Text text = new Text();
        text.setText(I18n.translate("engine.gui.main_menu.title"));
        text.setFont(new Font(Font.getDefaultFont(), 20));
        vBox.getChildren().add(text);

        Button butSP = new Button("Singleplayer");
        butSP.setBorder(new Border(Color.WHITE));
        butSP.setOnMouseClicked(event -> {
            var engine = Platform.getEngineClient();
            Path gameBasePath = engine.getRunPath().resolve("game");
            engine.getGraphicsManager().getGUIManager().show(new Scene(new GuiGameSelectSP(gameBasePath)));
        });
        vBox.getChildren().add(butSP);

        var butMP = new Button("Multiplayer");
        butMP.setOnMouseClicked(e -> {
            Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiDirectConnectServer()));
        });
        vBox.getChildren().add(butMP);

        var buttonSettings = new Button("Settings");
        buttonSettings.setOnMouseClicked(event ->
                Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiSettings())));
        vBox.getChildren().add(buttonSettings);

        Button buttonExit = new Button("Exit");
        buttonExit.setOnMouseClicked(event -> Platform.getEngine().terminate());
        vBox.getChildren().add(buttonExit);
    }
}
