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
        textField.setBorder(new Border(Color.BLACK, 0));
        textField.setBackground(Background.fromColor(Color.fromARGB(0x80000000)));
        AnchorPane.setLeftAnchor(textField, 3D);
        AnchorPane.setRightAnchor(textField, 3D);
        AnchorPane.setBottomAnchor(textField, 3D);
        textField.setPrefSize(USE_COMPUTED_VALUE, 23.0D);
        this.getChildren().add(textField);
        this.setBackground(Background.NOTHING);
        setOnKeyPressed(event -> {
            if (event.getKey() == KeyCode.ENTER) {
                String text = textField.text().get();
                if (text.startsWith("/")) { //command
                    //TODO go to command system

                } else {
                    // TODO go to server

                }
                Platform.getEngineClient().getGraphicsManager().getGUIManager().close();
            }
        });
    }
}
