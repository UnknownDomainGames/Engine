package engine.enginemod.client.gui.hud;

import engine.Platform;
import engine.block.Block;
import engine.client.EngineClient;
import engine.client.event.graphics.RenderEvent;
import engine.client.hud.HUDControl;
import engine.entity.Entity;
import engine.event.Listener;
import engine.graphics.GraphicsEngine;
import engine.graphics.GraphicsManager;
import engine.graphics.camera.Camera;
import engine.graphics.util.GPUInfo;
import engine.gui.control.Text;
import engine.gui.layout.AnchorPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Insets;
import engine.math.BlockPos;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
import org.joml.Vector3d;
import org.joml.Vector3fc;

import static engine.world.chunk.ChunkConstants.*;
import static java.lang.String.format;

public final class HUDDebug extends HUDControl {

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

    public HUDDebug() {
        name("debug");
        AnchorPane.setTopAnchor(this, 0f);
        AnchorPane.setLeftAnchor(this, 0f);

        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.setPadding(new Insets(5));
        setContent(vBox);

        fps = new Text();
        playerPosition = new Text();
        playerMotion = new Text();
        playerDirection = new Text();
        playerChunkPos = new Text();
        memory = new Text();
        gpuMemory = new Text();
        hitResult = new VBox();
        vBox.getChildren().addAll(fps, playerPosition, playerMotion, playerDirection, playerChunkPos, memory, gpuMemory, hitResult);

        hitBlockOrEntity = new Text();
        hitBlockOrEntityPos = new Text();
        hitPos = new Text();
        hitResult.getChildren().addAll(hitBlockOrEntity, hitBlockOrEntityPos, hitPos);
        hitResult.spacing().set(5);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        if (visible) Platform.getEngineClient().getEventBus().register(this);
        else Platform.getEngineClient().getEventBus().unregister(this);
    }

    @Listener
    public void update(RenderEvent.Pre event) {
        EngineClient engine = Platform.getEngineClient();
        Entity player = engine.getClientGame().getClientPlayer().getControlledEntity();
        GraphicsManager manager = engine.getGraphicsManager();

        fps.setText("FPS: " + manager.getFPS());
        playerPosition.setText(format("Player Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
        playerMotion.setText(format("Player Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
        playerDirection.setText(format("Player Direction (yaw, pitch, roll): %.2f, %.2f, %.2f (%s)", player.getRotation().x, player.getRotation().y, player.getRotation().z, getDirection(player.getRotation().x)));
        playerChunkPos.setText(format("Player At Chunk: %d, %d, %d", (int) Math.floor(player.getPosition().x) >> CHUNK_X_BITS, (int) Math.floor(player.getPosition().y) >> CHUNK_Y_BITS, (int) Math.floor(player.getPosition().z) >> CHUNK_Z_BITS));
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        memory.setText(format("Memory: %d MB / %d MB (Max: %d MB)", (totalMemory - freeMemory) / 1024 / 1024, totalMemory / 1024 / 1024, maxMemory / 1024 / 1024));
        GPUInfo gpuInfo = GraphicsEngine.getGraphicsBackend().getGPUInfo();
        gpuMemory.setText(format("GPU Memory: %d MB / %d MB", (gpuInfo.getTotalMemory() - gpuInfo.getFreeMemory()) / 1024, gpuInfo.getTotalMemory() / 1024));

        Camera camera = manager.getViewport().getCamera();
        HitResult hitResult = manager.getEngine().getClientGame().getClientWorld().raycast(camera.getPosition(), camera.getFront(), 10);
        if (hitResult.isFailure()) {
            this.hitResult.setVisible(false);
        } else if (hitResult instanceof EntityHitResult) {
            this.hitResult.setVisible(true);
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            hitBlockOrEntity.setText(String.format("Hit Entity: %s@%s", entity.getProvider().getName(), Integer.toHexString(entity.hashCode())));
            Vector3d position = entity.getPosition();
            hitBlockOrEntityPos.setText(String.format("Hit Entity Position: %.2f, %.2f, %.2f", position.x, position.y, position.z));
            Vector3fc hitPoint = ((EntityHitResult) hitResult).getHitPoint();
            hitPos.setText(String.format("Hit Entity Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
        } else if (hitResult instanceof BlockHitResult) {
            this.hitResult.setVisible(true);
            Block block = ((BlockHitResult) hitResult).getBlock();
            hitBlockOrEntity.setText(String.format("Hit Block: %s", block.getName()));
            BlockPos pos = ((BlockHitResult) hitResult).getPos();
            hitBlockOrEntityPos.setText(String.format("Hit Block Position: %d, %d, %d", pos.x(), pos.y(), pos.z()));
            Vector3fc hitPoint = ((BlockHitResult) hitResult).getHitPoint();
            hitPos.setText(String.format("Hit Block Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
        }
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
