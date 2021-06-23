package engine.enginemod.client.gui.game;

import engine.gui.control.Text;
import engine.gui.layout.FlowPane;
import engine.gui.misc.Background;
import engine.gui.misc.Pos;
import engine.util.Color;
import engine.world.chunk.ChunkPos;
import engine.world.chunk.ChunkStatus;
import engine.world.chunk.ChunkStatusListener;

public class GuiGameLoading extends FlowPane implements ChunkStatusListener {

    private final Text text2;
    private String[] strings = {"/", "|", "\\", "|"};
    private int index = 0;

    public GuiGameLoading() {
        alignment().set(Pos.CENTER);
        setBackground(new Background(Color.fromRGB(0xAAAAAA)));

        var text1 = new Text("Loading Game...");
        text2 = new Text(strings[index]);
        this.getChildren().addAll(text1, text2);
    }

    public void updateProgress() {
        index = ++index % strings.length;
        text2.setText(strings[index]);
    }

    @Override
    public void updateGraphCenter(ChunkPos pos) {

    }

    @Override
    public void onChunkStatusUpdate(ChunkPos pos, ChunkStatus status) {

    }

    @Override
    public void stop() {

    }
}
