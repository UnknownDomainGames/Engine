package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.graphics.RenderManager;
import engine.graphics.font.Font;
import engine.gui.GUIManager;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Pos;
import engine.gui.text.Text;
import engine.input.KeyCode;
import engine.util.Color;

public final class GUIPauseMenu extends FlowPane {

    public static Scene create() {
        Scene scene = new Scene(new GUIPauseMenu());
        scene.setOnKeyPressed(event -> {
            if (event.getKey() == KeyCode.ESCAPE) {
                RenderManager.instance().getGUIManager().close();
            }
        });
        return scene;
    }

    private GUIPauseMenu() {
        alignment().set(Pos.CENTER);
        background().set(new Background(Color.fromARGB(0x7FAAAAAA)));

        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.alignment().set(Pos.HPos.CENTER);
        getChildren().add(vBox);

        Text text = new Text();
        text.text().set("Game Menu");
        text.font().set(new Font(Font.getDefaultFont(), 20));
        vBox.getChildren().add(text);

        Button backtoGame = new Button("Back To Game");
        backtoGame.setOnMouseClicked(event -> Platform.getEngineClient().getRenderManager().getGUIManager().close());
        vBox.getChildren().add(backtoGame);

        Button terminateGame = new Button("Terminate");
        terminateGame.setOnMouseClicked(mouseClickEvent -> {
            var engine = Platform.getEngineClient();
            engine.getCurrentGame().terminate();
            GUIManager guiManager = engine.getRenderManager().getGUIManager();
            guiManager.show(new Scene(new GUIGameCreation()));
        });
        vBox.getChildren().add(terminateGame);
    }
}
