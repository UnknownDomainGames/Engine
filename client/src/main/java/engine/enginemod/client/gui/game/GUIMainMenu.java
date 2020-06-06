package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.client.ClientEngine;
import engine.client.game.StandaloneClientGame;
import engine.client.i18n.I18n;
import engine.enginemod.client.gui.GuiSettings;
import engine.game.GameData;
import engine.game.ServerGame;
import engine.graphics.font.Font;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Text;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.misc.Pos;
import engine.server.network.NetworkServer;
import engine.util.Color;
import engine.util.Files2;

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

        Button buttonCreate = new Button("New Game");
        buttonCreate.setBorder(new Border(Color.WHITE));
        buttonCreate.setOnMouseClicked(event -> {
            var engine = Platform.getEngineClient();
            engine.getGraphicsManager().getGUIManager().close();
            Path gameBasePath = engine.getRunPath().resolve("game");
            Files2.deleteDirectoryIfPresent(gameBasePath);
            runGame(engine, gameBasePath);
        });
        vBox.getChildren().add(buttonCreate);

        Button buttonLoad = new Button("Load Game");
        buttonLoad.setBorder(new Border(Color.WHITE));
        buttonLoad.setOnMouseClicked(event -> {
            var engine = Platform.getEngineClient();
            engine.getGraphicsManager().getGUIManager().close();
            Path gameBasePath = engine.getRunPath().resolve("game");
            engine.runServerGame(new StandaloneClientGame(engine, gameBasePath, GameData.createFromGame(gameBasePath)));
        });
        vBox.getChildren().add(buttonLoad);

        var buttonSettings = new Button("Settings");
        buttonSettings.setOnMouseClicked(event ->
                Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiSettings())));
        vBox.getChildren().add(buttonSettings);

        Button buttonExit = new Button("Exit");
        buttonExit.setOnMouseClicked(event -> Platform.getEngine().terminate());
        vBox.getChildren().add(buttonExit);

        var butCS = new Button("MultiPlayer");
        butCS.setOnMouseClicked(e -> {
            Platform.getEngineClient().getGraphicsManager().getGUIManager().show(new Scene(new GuiDirectConnectServer()));
        });
        vBox.getChildren().add(butCS);
    }

    private void runGame(ClientEngine engine, Path gameBasePath) {
        NetworkServer networkServer = new NetworkServer();
        networkServer.runLocal();
        GameData gameData = GameData.createFromCurrentEnvironment(gameBasePath, "default");
        engine.runServerGame(new ServerGame(engine, gameBasePath, gameData, networkServer));
        engine.runClientGame(new StandaloneClientGame(engine, gameBasePath, gameData));
    }

}
