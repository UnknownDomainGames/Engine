package unknowndomaingame.foundation.client.gui.hud;

import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.text.Text;
import nullengine.client.rendering.RenderContext;
import nullengine.entity.Entity;

import java.util.Collections;

public class HUDGameDebug extends VBox {

    private final Text fps;
    private final Text playerPosition;
    private final Text playerMotion;
    private final Text playerDirection;
    private final Text playerChunkPos;
    private final Text memory;

    private final VBox blockHitInfo;
    private final Text lookingBlock;
    private final Text lookingBlockPos;
    private final Text hitPos;

    public HUDGameDebug() {
        fps = new Text();
        playerPosition = new Text();
        playerMotion = new Text();
        playerDirection = new Text();
        playerChunkPos = new Text();
        memory = new Text();
        blockHitInfo = new VBox();

        Collections.addAll(getChildren(), fps, playerPosition, playerMotion, playerDirection, playerChunkPos, memory, blockHitInfo);
        spacing().set(5);
        padding().setValue(new Insets(5));

        lookingBlock = new Text();
        lookingBlockPos = new Text();
        hitPos = new Text();
        Collections.addAll(blockHitInfo.getChildren(), lookingBlock, lookingBlockPos, hitPos);
        blockHitInfo.spacing().set(5);
    }

    public void update(RenderContext context) {
        Entity player = context.getEngine().getCurrentGame().getPlayer().getControlledEntity();

        fps.text().setValue("FPS: " + context.getFPS());
        playerPosition.text().setValue(String.format("Player Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
        playerMotion.text().setValue(String.format("Player Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
        playerDirection.text().setValue(String.format("Player Direction (yaw, pitch, roll): %.2f, %.2f, %.2f", player.getRotation().x, player.getRotation().y, player.getRotation().z));
        playerChunkPos.text().setValue(String.format("Player At Chunk: %d, %d, %d", (int) player.getPosition().x >> 4, (int) player.getPosition().y >> 4, (int) player.getPosition().z >> 4));
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        memory.text().setValue(String.format("Memory: %dMB / %d MB (Max: %d MB)", (totalMemory - freeMemory) / 1024 / 1024, totalMemory / 1024 / 1024, maxMemory / 1024 / 1024));
//
//        blockHitInfo.visible().set(context.getHit() != null);
//        if (context.getHit() != null) {
//            RayTraceBlockHit hit = context.getHit();
//            lookingBlock.text().setValue(String.format("Looking block: %s", hit.getBlock().getUniqueName()));
//            lookingBlockPos.text().setValue(String.format("Looking pos: %s(%d, %d, %d)", hit.getFace().name(), hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ()));
//            hitPos.text().setValue(String.format("Looking at: (%.2f, %.2f, %.2f)", hit.getHitPoint().x, hit.getHitPoint().y, hit.getHitPoint().z));
//        }
    }
}
