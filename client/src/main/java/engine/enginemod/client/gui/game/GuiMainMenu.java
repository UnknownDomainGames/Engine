package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.client.EngineClientImpl;
import engine.client.i18n.I18n;
import engine.enginemod.client.gui.GuiSettings;
import engine.game.GameData;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Text;
import engine.gui.layout.BorderPane;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Pos;
import engine.util.Color;

import java.nio.file.Path;

public class GuiMainMenu extends BorderPane {

    public GuiMainMenu() {
        setBackground(new Background(Color.fromRGB(0xAAAAAA)));

        { // Center Buttons
            var flowLayout = new FlowPane();
            flowLayout.alignment().set(Pos.CENTER);

            var vLayout = new VBox();
            vLayout.setSpacing(5);
            vLayout.setFillWidth(true);

            flowLayout.getChildren().add(vLayout);
            center().set(flowLayout);

            { // Title
                Text text = new Text(I18n.translate("engine.gui.main_menu.title"));
                text.setFontSize(20);
                vLayout.getChildren().add(text);
            }

            { // Single Player
                Button button = new Button(I18n.translate("engine.gui.main_menu.single_player"));
                button.setOnMouseClicked(event ->
                        Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiGameSelectSP())));
                vLayout.getChildren().add(button);
            }

            { // Multi Player
                Button button = new Button(I18n.translate("engine.gui.main_menu.multi_player"));
                button.setOnMouseClicked(event ->
                        Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiDirectConnectServer())));
                vLayout.getChildren().add(button);
            }

            { // Settings
                Button button = new Button(I18n.translate("engine.gui.main_menu.settings"));
                button.setOnMouseClicked(event ->
                        Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiSettings())));
                vLayout.getChildren().add(button);
            }

            { // Exit
                Button button = new Button(I18n.translate("engine.gui.main_menu.exit"));
                button.setOnMouseClicked(event ->
                        Platform.getEngine().terminate());
                vLayout.getChildren().add(button);
            }

            { // Test WorldGen
                Button button = new Button(I18n.translate("Test WorldGen"));
                button.setOnMouseClicked(event -> {
                    var gameData = GameData.createFromCurrentEnvironment(Path.of(""), "DEBUG");
                    gameData.getWorlds().put("Debug", "engine:debug");
                    ((EngineClientImpl) Platform.getEngineClient()).playIntegratedGame("DEBUG", gameData);
                });
                vLayout.getChildren().add(button);
            }
        }

    }
}
