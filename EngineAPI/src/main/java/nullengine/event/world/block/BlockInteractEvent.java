package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.EventBase;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3fc;

public abstract class BlockInteractEvent extends EventBase.Cancellable {
    private final World world;
    private final BlockPos pos;
    private final Block block;
    private final Vector3fc hit;

    public BlockInteractEvent(World world, BlockPos pos, Block block, Vector3fc hit) {
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.hit = hit;
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

    public Vector3fc getHit() {
        return hit;
    }

    public static class Pre extends BlockInteractEvent {
        public Pre(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }

    public static class Post extends BlockInteractEvent {
        public Post(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }
}

