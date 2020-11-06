package engine.world.hit;

import engine.block.state.BlockState;
import engine.game.Game;
import engine.math.BlockPos;
import engine.util.Direction;
import engine.world.World;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class BlockHitResult extends HitResult {

    private static final BlockHitResult FAILURE = new BlockHitResult();

    public static BlockHitResult failure() {
        return FAILURE;
    }

    private final World world;
    private final BlockPos pos;
    private final BlockState block;
    private final Vector3fc hitPoint;
    private final Direction direction;

    public BlockHitResult(World world, BlockPos pos, BlockState block, Vector3fc hitPoint, Direction direction) {
        this(Type.BLOCK, world, pos, block, hitPoint, direction);
    }

    private BlockHitResult() {
        this(Type.MISS, null, null, null, null, null);
    }

    private BlockHitResult(Type type, World world, BlockPos pos, BlockState block, Vector3fc hitPoint, Direction direction) {
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

    public BlockState getBlock() {
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

    public Simplified simplify(){
        return new Simplified(world.getName(), pos,hitPoint, direction);
    }

    public static class Simplified extends BlockHitResult {
        private final String worldName;

        public Simplified(String worldName, BlockPos pos, Vector3fc hitPoint, Direction direction) {
            super(null, pos, null, hitPoint, direction);
            this.worldName = worldName;
        }

        public String getWorldName() {
            return worldName;
        }

        public BlockHitResult build(Game game) {
            var worldOptional = game.getWorld(worldName);
            var blockOptional = worldOptional.map(world1 -> world1.getBlock(getPos()));
            return new BlockHitResult(worldOptional.orElse(null), getPos(), blockOptional.orElse(null), getHitPoint(), getDirection());
        }

        @Override
        public Simplified simplify() {
            return this; //prevent creating another simplified version
        }
    }
}
