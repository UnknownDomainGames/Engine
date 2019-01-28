package unknowndomain.engine.client._gui;

import unknowndomain.engine.client.ClientContext;
import unknowndomain.engine.client.gui.layout.VBox;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.entity.Entity;

import java.util.Collections;

public class DebugHUD extends VBox {

    private final Text fps;
    private final Text playerPosition;
    private final Text playerMotion;
    private final Text playerDirection;
    private final Text playerChunkPos;

    public DebugHUD() {
        fps = new Text();
        playerPosition = new Text();
        playerMotion = new Text();
        playerDirection = new Text();
        playerChunkPos = new Text();
        Collections.addAll(getChildren(), fps, playerPosition, playerMotion, playerDirection, playerChunkPos);

        spacing().set(5);
        padding().setValue(new Insets(5));
    }

    public void update(ClientContext context) {
        Entity player = context.getPlayer().getControlledEntity();

        fps.text().setValue("FPS: " + context.getFps());
        playerPosition.text().setValue(String.format("Player Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
        playerMotion.text().setValue(String.format("Player Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
        playerDirection.text().setValue(String.format("Player Direction (yaw, pitch, roll): %.2f, %.2f, %.2f", player.getRotation().x, player.getRotation().y, player.getRotation().z));
        playerChunkPos.text().setValue(String.format("Player At Chunk: %d, %d, %d", (int) player.getPosition().x >> 4, (int) player.getPosition().y >> 4, (int) player.getPosition().z >> 4));
    }
}
