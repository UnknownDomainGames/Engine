package unknowndomain.game.client.gui.game;

import unknowndomain.engine.client.gui.component.TextField;
import unknowndomain.engine.client.gui.event.KeyEvent;
import unknowndomain.engine.client.gui.layout.AnchorPane;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Border;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.util.Color;

public class GuiChat extends AnchorPane {
    private TextField textField;
    private final Game game;
    public GuiChat(Game game){
        this.game = game;
        textField = new TextField();
        textField.forceFocus();
        textField.border().setValue(new Border(Color.BLACK, 0f));
        textField.background().setValue(Background.fromColor(Color.fromARGB(0x80000000)));
        AnchorPane.setLeftAnchor(textField,3f);
        AnchorPane.setRightAnchor(textField,3f);
        AnchorPane.setBottomAnchor(textField,3f);
        textField.getSize().prefHeight().set(23.0f);
        this.getChildren().add(textField);
        this.background().setValue(Background.NOTHING);
        this.addEventHandler(KeyEvent.KeyDownEvent.class, event->{
            if(event.getKey() == Key.KEY_ENTER){
                String text = textField.text().getValue();
                if(text.startsWith("/")){ //command
                    //TODO go to command system

                }else{
                    // TODO go to server

                }
                requireCLose();
            }
        });
    }
}
