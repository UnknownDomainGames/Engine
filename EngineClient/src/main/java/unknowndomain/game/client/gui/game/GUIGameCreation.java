package unknowndomain.game.client.gui.game;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.gui.component.Button;
import unknowndomain.engine.client.gui.layout.BorderPane;
import unknowndomain.engine.client.gui.layout.VBox;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.game.GameDefinitionImpl;
import unknowndomain.engine.player.PlayerImpl;
import unknowndomain.engine.player.Profile;
import unknowndomain.game.Blocks;

import java.util.UUID;

public class GUIGameCreation extends BorderPane {

    public GUIGameCreation() {
        VBox vBox = new VBox();
        center().setValue(vBox);

        Text textGameCreation = new Text("Game Creation");
        vBox.getChildren().add(textGameCreation);

        Button buttonCreate = new Button("Create");
        buttonCreate.setOnClick(mouseClickEvent -> {
            var engine = Platform.getEngineClient();
            var player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
            engine.startGame(new GameClientStandalone(engine, new GameDefinitionImpl(Blocks.AIR), player));
            engine.getRenderContext().getGuiManager().closeScreen();
        });
        vBox.getChildren().add(buttonCreate);
    }
}
