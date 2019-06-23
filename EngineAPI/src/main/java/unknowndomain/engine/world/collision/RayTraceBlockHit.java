package unknowndomain.engine.world.collision;

import org.joml.Vector3f;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.World;

import java.util.function.Consumer;

public class RayTraceBlockHit {

    private static final RayTraceBlockHit FAILURE = new RayTraceBlockHit(null, null, null, null, null);

    public static RayTraceBlockHit failure() {
        return FAILURE;
    }

    private final World world;
    private final BlockPos pos;
    private final Block block;
    private final Vector3f hitPoint;
    private final Facing face;

    public RayTraceBlockHit(World world, BlockPos pos, Block block, Vector3f hitPoint, Facing face) {
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.hitPoint = hitPoint;
        this.face = face;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }

    public Vector3f getHitPoint() {
        return hitPoint;
    }

    public Facing getFace() {
        return face;
    }

    public boolean isSuccess() {
        return block != null;
    }

    public void ifSuccess(Consumer<RayTraceBlockHit> consumer) {
        if (isSuccess())
            consumer.accept(this);
    }
}
