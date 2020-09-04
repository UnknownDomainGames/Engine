package engine.enginemod.client.gui.game;

import engine.gui.control.Text;
import engine.gui.layout.FlowPane;

public class GuiGameLoading extends FlowPane {

    private final Text text2;
    private String[] strings = {"/", "|", "\\", "|"};
    private int index = 0;

    public GuiGameLoading() {
        var text1 = new Text("Loading Game...");
        text2 = new Text(strings[index]);
        this.getChildren().addAll(text1, text2);
    }

    public void updateProgress() {
        index = ++index % strings.length;
        text2.setText(strings[index]);
    }
}
