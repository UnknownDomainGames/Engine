package engine.enginemod.client.gui.hud;

import engine.client.event.rendering.RenderEvent;
import engine.event.Listener;
import engine.gui.layout.VBox;
import engine.gui.misc.Insets;
import engine.gui.text.Text;

import static java.lang.String.format;

public class HUDGameDebug extends VBox {

    private final Text fps;
    private final Text playerPosition;
    private final Text playerMotion;
    private final Text playerDirection;
    private final Text playerChunkPos;
    private final Text memory;
    private final Text gpuMemory;

    private final VBox hitResult;
    private final Text hitBlockOrEntity;
    private final Text hitBlockOrEntityPos;
    private final Text hitPos;

    public HUDGameDebug() {
        fps = new Text();
        playerPosition = new Text();
        playerMotion = new Text();
        playerDirection = new Text();
        playerChunkPos = new Text();
        memory = new Text();
        gpuMemory = new Text();
        hitResult = new VBox();

        getChildren().addAll(fps, playerPosition, playerMotion, playerDirection, playerChunkPos, memory, gpuMemory, hitResult);
        spacing().set(5);
        padding().setValue(new Insets(5));

        hitBlockOrEntity = new Text();
        hitBlockOrEntityPos = new Text();
        hitPos = new Text();
        hitResult.getChildren().addAll(hitBlockOrEntity, hitBlockOrEntityPos, hitPos);
        hitResult.spacing().set(5);
    }

    @Listener
    public void update(RenderEvent.Pre event) {
//        Entity player = Platform.getEngineClient().getCurrentGame().getClientPlayer().getControlledEntity();
//
//        fps.text().setValue("FPS: " + context.getFPS());
//        playerPosition.text().setValue(format("Player Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
//        playerMotion.text().setValue(format("Player Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
//        playerDirection.text().setValue(format("Player Direction (yaw, pitch, roll): %.2f, %.2f, %.2f (%s)", player.getRotation().x, player.getRotation().y, player.getRotation().z, getDirection(player.getRotation().x)));
//        playerChunkPos.text().setValue(format("Player At Chunk: %d, %d, %d", (int) Math.floor(player.getPosition().x) >> CHUNK_X_BITS, (int) Math.floor(player.getPosition().y) >> CHUNK_Y_BITS, (int) Math.floor(player.getPosition().z) >> CHUNK_Z_BITS));
//        Runtime runtime = Runtime.getRuntime();
//        long totalMemory = runtime.totalMemory();
//        long maxMemory = runtime.maxMemory();
//        long freeMemory = runtime.freeMemory();
//        memory.text().setValue(format("Memory: %d MB / %d MB (Max: %d MB)", (totalMemory - freeMemory) / 1024 / 1024, totalMemory / 1024 / 1024, maxMemory / 1024 / 1024));
//        GPUInfo gpuInfo = context.getGPUInfo();
//        gpuMemory.text().setValue(format("GPU Memory: %d MB / %d MB", (gpuInfo.getTotalMemory() - gpuInfo.getFreeMemory()) / 1024, gpuInfo.getTotalMemory() / 1024));
////
////        blockHitInfo.visible().set(context.getHit() != null);
////        if (context.getHit() != null) {
////            RayTraceBlockHit hit = context.getHit();
////            lookingBlock.text().setValue(String.format("Looking block: %s", hit.getBlock().getUniqueName()));
////            lookingBlockPos.text().setValue(String.format("Looking pos: %s(%d, %d, %d)", hit.getFace().name(), hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ()));
////            hitPos.text().setValue(String.format("Looking at: (%.2f, %.2f, %.2f)", hit.getHitPoint().x, hit.getHitPoint().y, hit.getHitPoint().z));
////        }
//
//        Camera camera = context.getCamera();
//        HitResult hitResult = context.getEngine().getCurrentGame().getClientWorld().raycast(camera.getPosition(), camera.getFront(), 10);
//        if (hitResult.isFailure()) {
//            this.hitResult.visible().set(false);
//        } else if (hitResult instanceof EntityHitResult) {
//            this.hitResult.visible().set(true);
//            Entity entity = ((EntityHitResult) hitResult).getEntity();
//            hitBlockOrEntity.text().setValue(String.format("Hit Entity: %s@%s", entity.getProvider().getName(), Integer.toHexString(entity.hashCode())));
//            Vector3d position = entity.getPosition();
//            hitBlockOrEntityPos.text().setValue(String.format("Hit Entity Position: %.2f, %.2f, %.2f", position.x, position.y, position.z));
//            Vector3fc hitPoint = ((EntityHitResult) hitResult).getHitPoint();
//            hitPos.text().setValue(String.format("Hit Entity Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
//        } else if (hitResult instanceof BlockHitResult) {
//            this.hitResult.visible().set(true);
//            Block block = ((BlockHitResult) hitResult).getBlock();
//            hitBlockOrEntity.text().setValue(String.format("Hit Block: %s", block.getName()));
//            BlockPos pos = ((BlockHitResult) hitResult).getPos();
//            hitBlockOrEntityPos.text().setValue(String.format("Hit Block Position: %d, %d, %d", pos.x(), pos.y(), pos.z()));
//            Vector3fc hitPoint = ((BlockHitResult) hitResult).getHitPoint();
//            hitPos.text().setValue(String.format("Hit Block Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
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
