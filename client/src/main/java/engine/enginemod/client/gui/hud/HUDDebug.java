package engine.enginemod.client.gui.hud;

import engine.Platform;
import engine.block.Block;
import engine.client.EngineClient;
import engine.client.hud.HUDControl;
import engine.entity.Entity;
import engine.graphics.GraphicsEngine;
import engine.graphics.GraphicsManager;
import engine.graphics.camera.Camera;
import engine.graphics.util.GPUInfo;
import engine.gui.control.Text;
import engine.gui.layout.AnchorPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Insets;
import engine.math.BlockPos;
import engine.world.chunk.Chunk;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
import org.joml.Vector3d;
import org.joml.Vector3fc;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static java.lang.String.format;

public final class HUDDebug extends HUDControl {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    private final Text fps;
    private final Text position;
    private final Text motion;
    private final Text direction;
    private final Text chunk;
    private final Text heapMemory;
    private final Text nonHeapMemory;
    private final Text gpuMemory;

    private final VBox hitResult;
    private final Text hitBlockOrEntity;
    private final Text hitBlockOrEntityPos;
    private final Text hitPos;

    public HUDDebug() {
        name("debug");
        AnchorPane.setTopAnchor(this, 0D);
        AnchorPane.setLeftAnchor(this, 0D);

        VBox vBox = new VBox();
        vBox.spacing().set(5);
        vBox.setPadding(new Insets(5));
        setContent(vBox);

        fps = new Text();
        position = new Text();
        motion = new Text();
        direction = new Text();
        chunk = new Text();
        heapMemory = new Text();
        nonHeapMemory = new Text();
        gpuMemory = new Text();
        hitResult = new VBox();
        vBox.getChildren().addAll(fps, position, motion, direction, chunk, heapMemory, nonHeapMemory, gpuMemory, hitResult);

        hitBlockOrEntity = new Text();
        hitBlockOrEntityPos = new Text();
        hitPos = new Text();
        hitResult.getChildren().addAll(hitBlockOrEntity, hitBlockOrEntityPos, hitPos);
        hitResult.spacing().set(5);
    }

    @Override
    public void update() {
        EngineClient engine = Platform.getEngineClient();
        if (engine.getCurrentClientGame() == null) return;
        Entity player = engine.getCurrentClientGame().getClientPlayer().getControlledEntity();
        GraphicsManager manager = engine.getGraphicsManager();

        fps.setText("FPS: " + manager.getFPS());
        position.setText(format("Position: %.2f, %.2f, %.2f", player.getPosition().x, player.getPosition().y, player.getPosition().z));
        motion.setText(format("Motion: %.2f, %.2f, %.2f", player.getMotion().x, player.getMotion().y, player.getMotion().z));
        direction.setText(format("Direction (yaw, pitch, roll): %.2f, %.2f, %.2f (%s)", player.getRotation().x, player.getRotation().y, player.getRotation().z, getDirection(player.getRotation().x)));
        chunk.setText(format("Chunk: %d, %d, %d", (int) Math.floor(player.getPosition().x) >> Chunk.CHUNK_X_BITS, (int) Math.floor(player.getPosition().y) >> Chunk.CHUNK_Y_BITS, (int) Math.floor(player.getPosition().z) >> Chunk.CHUNK_Z_BITS));

        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        heapMemory.setText(format("Memory: %d MB / %d MB (Max: %d MB)", toMiB(heapMemoryUsage.getUsed()), toMiB(heapMemoryUsage.getCommitted()), toMiB(heapMemoryUsage.getMax())));
        nonHeapMemory.setText(format("Non-Heap Memory: %d MB / %d MB", toMiB(nonHeapMemoryUsage.getUsed()), toMiB(nonHeapMemoryUsage.getCommitted())));

        GPUInfo gpuInfo = GraphicsEngine.getGraphicsBackend().getGPUInfo();
        gpuMemory.setText(format("GPU Memory: %d MB / %d MB", toMiB(gpuInfo.getUsedMemory()), toMiB(gpuInfo.getTotalMemory())));

        Camera camera = manager.getViewport().getCamera();
        HitResult hitResult = manager.getEngine().getCurrentClientGame().getClientWorld().raycast(camera.getPosition(), camera.getFront(), 10);
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
            Block block = ((BlockHitResult) hitResult).getBlock().getPrototype();
            hitBlockOrEntity.setText(String.format("Hit Block: %s", block.getName()));
            BlockPos pos = ((BlockHitResult) hitResult).getPos();
            hitBlockOrEntityPos.setText(String.format("Hit Block Position: %d, %d, %d", pos.x(), pos.y(), pos.z()));
            Vector3fc hitPoint = ((BlockHitResult) hitResult).getHitPoint();
            hitPos.setText(String.format("Hit Block Point: %.2f, %.2f, %.2f", hitPoint.x(), hitPoint.y(), hitPoint.z()));
        }
    }

    private static long toMiB(long bytes) {
        return bytes >> 20;
    }

    private static String getDirection(float x) {
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
