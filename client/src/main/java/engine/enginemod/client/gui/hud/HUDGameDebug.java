package engine.enginemod.client.gui.hud;

import engine.Platform;
import engine.block.Block;
import engine.client.EngineClient;
import engine.client.event.rendering.RenderEvent;
import engine.client.hud.HUDControl;
import engine.entity.Entity;
import engine.event.Listener;
import engine.graphics.GraphicsEngine;
import engine.graphics.RenderManager;
import engine.graphics.camera.Camera;
import engine.graphics.util.GPUInfo;
import engine.gui.layout.AnchorPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Insets;
import engine.gui.text.Text;
import engine.math.BlockPos;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
import org.joml.Vector3d;
import org.joml.Vector3fc;

import static engine.world.chunk.ChunkConstants.*;
import static java.lang.String.format;

public class HUDGameDebug extends HUDControl {

    private final VBox vBox;
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
        super("GameDebug");
        AnchorPane.setTopAnchor(this, 0f);
        AnchorPane.setLeftAnchor(this, 0f);
        visible().addChangeListener((observable, oldValue, newValue) -> {
            if (newValue) Platform.getEngineClient().getEventBus().register(this);
            else Platform.getEngineClient().getEventBus().unregister(this);
        });

        vBox = new VBox();
        vBox.spacing().set(5);
        vBox.padding().set(new Insets(5));
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

    @Listener
    public void update(RenderEvent.Pre event) {
        EngineClient engine = Platform.getEngineClient();
        Entity player = engine.getCurrentGame().getClientPlayer().getControlledEntity();
        RenderManager manager = engine.getRenderManager();

        fps.text().set("FPS: " + manager.getFPS());
        playerPosition.text().set(format("Player Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
        playerMotion.text().set(format("Player Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
        playerDirection.text().set(format("Player Direction (yaw, pitch, roll): %.2f, %.2f, %.2f (%s)", player.getRotation().x, player.getRotation().y, player.getRotation().z, getDirection(player.getRotation().x)));
        playerChunkPos.text().set(format("Player At Chunk: %d, %d, %d", (int) Math.floor(player.getPosition().x) >> CHUNK_X_BITS, (int) Math.floor(player.getPosition().y) >> CHUNK_Y_BITS, (int) Math.floor(player.getPosition().z) >> CHUNK_Z_BITS));
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        memory.text().set(format("Memory: %d MB / %d MB (Max: %d MB)", (totalMemory - freeMemory) / 1024 / 1024, totalMemory / 1024 / 1024, maxMemory / 1024 / 1024));
        GPUInfo gpuInfo = GraphicsEngine.getGraphicsBackend().getGPUInfo();
        gpuMemory.text().set(format("GPU Memory: %d MB / %d MB", (gpuInfo.getTotalMemory() - gpuInfo.getFreeMemory()) / 1024, gpuInfo.getTotalMemory() / 1024));

        Camera camera = manager.getViewport().getCamera();
        HitResult hitResult = manager.getEngine().getCurrentGame().getClientWorld().raycast(camera.getPosition(), camera.getFront(), 10);
        if (hitResult.isFailure()) {
            this.hitResult.visible().set(false);
        } else if (hitResult instanceof EntityHitResult) {
            this.hitResult.visible().set(true);
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            hitBlockOrEntity.text().set(String.format("Hit Entity: %s@%s", entity.getProvider().getName(), Integer.toHexString(entity.hashCode())));
            Vector3d position = entity.getPosition();
            hitBlockOrEntityPos.text().set(String.format("Hit Entity Position: %.2f, %.2f, %.2f", position.x, position.y, position.z));
            Vector3fc hitPoint = ((EntityHitResult) hitResult).getHitPoint();
            hitPos.text().set(String.format("Hit Entity Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
        } else if (hitResult instanceof BlockHitResult) {
            this.hitResult.visible().set(true);
            Block block = ((BlockHitResult) hitResult).getBlock();
            hitBlockOrEntity.text().set(String.format("Hit Block: %s", block.getName()));
            BlockPos pos = ((BlockHitResult) hitResult).getPos();
            hitBlockOrEntityPos.text().set(String.format("Hit Block Position: %d, %d, %d", pos.x(), pos.y(), pos.z()));
            Vector3fc hitPoint = ((BlockHitResult) hitResult).getHitPoint();
            hitPos.text().set(String.format("Hit Block Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
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
