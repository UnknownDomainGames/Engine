package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.client.EngineClientImpl;
import engine.client.i18n.I18n;
import engine.graphics.GraphicsManager;
import engine.graphics.font.Font;
import engine.gui.GUIManager;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Text;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.HPos;
import engine.gui.misc.Pos;
import engine.input.KeyCode;
import engine.util.Color;

public final class GuiPauseMenu extends FlowPane {

    public static Scene create() {
        Scene scene = new Scene(new GuiPauseMenu());
        scene.setOnKeyPressed(event -> {
            if (event.getKey() == KeyCode.ESCAPE) {
                returnToGame();
            }
        });
        return scene;
    }

    private static void returnToGame() {
        Platform.getEngineClient().setGamePauseState(false);
        GraphicsManager.instance().getGUIManager().close();
    }

    private GuiPauseMenu() {
        alignment().set(Pos.CENTER);
        setBackground(new Background(Color.fromARGB(0x7FAAAAAA)));

        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.alignment().set(HPos.CENTER);
        getChildren().add(vBox);

        Text text = new Text();
        text.setText(I18n.translate("engine.gui.pause_menu.title"));
        text.setFont(new Font(Font.getDefaultFont(), 20));
        vBox.getChildren().add(text);

        Button backToGame = new Button(I18n.translate("engine.gui.pause_menu.back"));
        backToGame.setOnMouseClicked(event -> returnToGame());
        vBox.getChildren().add(backToGame);

        Button terminateGame = new Button(I18n.translate("engine.gui.pause_menu.terminate"));
        terminateGame.setOnMouseClicked(mouseClickEvent -> {
            var engine = Platform.getEngineClient();
            engine.getCurrentClientGame().terminate();
            if (((EngineClientImpl) engine).isIntegratedServerRunning()) {
                ((EngineClientImpl) engine).stopIntegratedGame();
                // move gui handling to method
            } else {
                GUIManager guiManager = engine.getGraphicsManager().getGUIManager();
                guiManager.show(new Scene(new GuiMainMenu()));
            }
        });
        vBox.getChildren().add(terminateGame);
    }
}
