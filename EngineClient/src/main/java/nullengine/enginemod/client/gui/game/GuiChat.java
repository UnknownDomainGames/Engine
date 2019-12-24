package nullengine.enginemod.client.gui.game;

import nullengine.Platform;
import nullengine.client.gui.component.TextField;
import nullengine.client.gui.input.KeyCode;
import nullengine.client.gui.layout.AnchorPane;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Border;
import nullengine.game.Game;
import nullengine.util.Color;

public class GuiChat extends AnchorPane {
    private TextField textField;
    private final Game game;

    public GuiChat(Game game) {
        this.game = game;
        textField = new TextField();
        textField.forceFocus();
        textField.border().setValue(new Border(Color.BLACK, 0f));
        textField.background().setValue(Background.fromColor(Color.fromARGB(0x80000000)));
        AnchorPane.setLeftAnchor(textField, 3f);
        AnchorPane.setRightAnchor(textField, 3f);
        AnchorPane.setBottomAnchor(textField, 3f);
        textField.getSize().prefHeight().set(23.0f);
        this.getChildren().add(textField);
        this.background().setValue(Background.NOTHING);
        setOnKeyPressed(event -> {
            if (event.getKey() == KeyCode.KEY_ENTER) {
                String text = textField.text().getValue();
                if (text.startsWith("/")) { //command
                    //TODO go to command system

                } else {
                    // TODO go to server

                }
                Platform.getEngineClient().getRenderManager().getGuiManager().closeScreen();
            }
        });
    }
}
