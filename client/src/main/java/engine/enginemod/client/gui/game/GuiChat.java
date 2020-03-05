package engine.enginemod.client.gui.game;

import engine.Platform;
import engine.game.Game;
import engine.gui.control.TextField;
import engine.gui.layout.AnchorPane;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.input.KeyCode;
import engine.util.Color;

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
            if (event.getKey() == KeyCode.ENTER) {
                String text = textField.text().get();
                if (text.startsWith("/")) { //command
                    //TODO go to command system

                } else {
                    // TODO go to server

                }
                Platform.getEngineClient().getRenderManager().getGUIManager().close();
            }
        });
    }
}
