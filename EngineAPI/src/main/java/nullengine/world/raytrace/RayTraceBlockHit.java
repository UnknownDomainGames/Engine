package nullengine.world.raytrace;

import nullengine.block.Block;
import nullengine.math.BlockPos;
import nullengine.util.Direction;
import nullengine.world.World;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class RayTraceBlockHit {

    private static final RayTraceBlockHit FAILURE = new RayTraceBlockHit(null, null, null, null, null);

    public static RayTraceBlockHit failure() {
        return FAILURE;
    }

    private final World world;
    private final BlockPos pos;
    private final Block block;
    private final Vector3fc hitPoint;
    private final Direction direction;

    public RayTraceBlockHit(World world, BlockPos pos, Block block, Vector3fc hitPoint, Direction direction) {
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.hitPoint = hitPoint;
        this.direction = direction;
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

    public Vector3fc getHitPoint() {
        return hitPoint;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isSuccess() {
        return block != null;
    }

    public void ifSuccess(Consumer<RayTraceBlockHit> consumer) {
        if (isSuccess())
            consumer.accept(this);
    }
}
