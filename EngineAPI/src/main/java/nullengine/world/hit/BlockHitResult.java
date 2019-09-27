package nullengine.world.hit;

import nullengine.block.Block;
import nullengine.math.BlockPos;
import nullengine.util.Direction;
import nullengine.world.World;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class BlockHitResult extends HitResult {

    private static final BlockHitResult FAILURE = new BlockHitResult();

    public static BlockHitResult failure() {
        return FAILURE;
    }

    private final World world;
    private final BlockPos pos;
    private final Block block;
    private final Vector3fc hitPoint;
    private final Direction direction;

    public BlockHitResult(World world, BlockPos pos, Block block, Vector3fc hitPoint, Direction direction) {
        this(Type.BLOCK, world, pos, block, hitPoint, direction);
    }

    private BlockHitResult() {
        this(Type.MISS, null, null, null, null, null);
    }

    private BlockHitResult(Type type, World world, BlockPos pos, Block block, Vector3fc hitPoint, Direction direction) {
        super(type);
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

    public void ifSuccess(Consumer<BlockHitResult> consumer) {
        if (isSuccess())
            consumer.accept(this);
    }
}
