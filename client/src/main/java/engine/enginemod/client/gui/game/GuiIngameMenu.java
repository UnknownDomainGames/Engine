package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.graphics.font.Font;
import engine.gui.GUIManager;
import engine.gui.Scene;
import engine.gui.control.Button;
import engine.gui.control.Label;
import engine.gui.layout.BorderPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Background;
import engine.gui.misc.Insets;
import engine.gui.misc.Pos;
import engine.util.Color;

public class GuiIngameMenu extends BorderPane {
    public GuiIngameMenu() {
        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.alignment().setValue(Pos.HPos.CENTER);
        center().setValue(vBox);
        this.background().setValue(new Background(Color.fromARGB(0x7FAAAAAA)));
        vBox.padding().setValue(new Insets(100, 350, 0, 350));

        Label text = new Label();
        text.text().setValue("Game Menu");
        text.font().setValue(new Font(Font.getDefaultFont(), 20));
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
