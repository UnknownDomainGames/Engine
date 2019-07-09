package nullengine.enginemod.client.gui.hud;

import nullengine.client.gui.GuiTickable;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.text.Text;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.util.GPUMemoryInfo;
import nullengine.entity.Entity;

import static java.lang.String.format;

public class HUDGameDebug extends VBox implements GuiTickable {

    private final Text fps;
    private final Text playerPosition;
    private final Text playerMotion;
    private final Text playerDirection;
    private final Text playerChunkPos;
    private final Text memory;
    private final Text gpuMemory;

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
        gpuMemory = new Text();
        blockHitInfo = new VBox();

        getChildren().addAll(fps, playerPosition, playerMotion, playerDirection, playerChunkPos, memory, gpuMemory, blockHitInfo);
        spacing().set(5);
        padding().setValue(new Insets(5));

        lookingBlock = new Text();
        lookingBlockPos = new Text();
        hitPos = new Text();
        blockHitInfo.getChildren().addAll(lookingBlock, lookingBlockPos, hitPos);
        blockHitInfo.spacing().set(5);
    }

    public void update(RenderContext context) {
        Entity player = context.getEngine().getCurrentGame().getPlayer().getControlledEntity();

        fps.text().setValue("FPS: " + context.getFPS());
        playerPosition.text().setValue(format("Player Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
        playerMotion.text().setValue(format("Player Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
        playerDirection.text().setValue(format("Player Direction (yaw, pitch, roll): %.2f, %.2f, %.2f (%s)", player.getRotation().x, player.getRotation().y, player.getRotation().z, getDirection(player.getRotation().x)));
        playerChunkPos.text().setValue(format("Player At Chunk: %d, %d, %d", (int) player.getPosition().x >> 4, (int) player.getPosition().y >> 4, (int) player.getPosition().z >> 4));
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        memory.text().setValue(format("Memory: %d MB / %d MB (Max: %d MB)", (totalMemory - freeMemory) / 1024 / 1024, totalMemory / 1024 / 1024, maxMemory / 1024 / 1024));
        GPUMemoryInfo gpuMemoryInfo = context.getGPUMemoryInfo();
        gpuMemory.text().setValue(format("GPU Memory: %d MB / %d MB", (gpuMemoryInfo.getTotalMemory() - gpuMemoryInfo.getFreeMemory()) / 1024, gpuMemoryInfo.getTotalMemory() / 1024));
//
//        blockHitInfo.visible().set(context.getHit() != null);
//        if (context.getHit() != null) {
//            RayTraceBlockHit hit = context.getHit();
//            lookingBlock.text().setValue(String.format("Looking block: %s", hit.getBlock().getUniqueName()));
//            lookingBlockPos.text().setValue(String.format("Looking pos: %s(%d, %d, %d)", hit.getFace().name(), hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ()));
//            hitPos.text().setValue(String.format("Looking at: (%.2f, %.2f, %.2f)", hit.getHitPoint().x, hit.getHitPoint().y, hit.getHitPoint().z));
//        }
    }

    private String getDirection(float x) {
        float roundedX = Math.round(x * 100f) / 100f;
        if (roundedX == 0 || roundedX == 360) {
            return "E";
        } else if (roundedX < 90) {
            return format("N%.2fE", 90 - roundedX);
        } else if (roundedX == 90) {
            return "N";
        } else if (roundedX < 180) {
            return format("N%.2fW", roundedX - 90);
        } else if (roundedX == 180) {
            return "W";
        } else if (roundedX < 270) {
            return format("S%.2fW", 270 - roundedX);
        } else if (roundedX == 270) {
            return "S";
        } else {
            return format("S%.2fE", roundedX - 270);
        }
    }
}
