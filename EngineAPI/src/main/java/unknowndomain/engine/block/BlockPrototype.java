package unknowndomain.engine.block;

import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import java.util.List;

public abstract class BlockPrototype {
    // all these behaviors are missing arguments
    // fill those arguments later

    public static PlaceBehavior DEFAULT_PLACE = (world, entity, blockPos, block) -> {
    };
    public static ActiveBehavior DEFAULT_ACTIVE = (world, entity, pos, block) -> {
    };
    public static TouchBehavior DEFAULT_TOUCH = new TouchBehavior() {
        @Override
        public boolean onTouch(Block block) {
            return true;
        }

        @Override
        public void onTouched(Block block) {

        }
    };
    public static DestroyBehavior DEFAULT_DESTROY = (world, entity, blockPos, block) -> {
    };

    public abstract List<Block> getAllStates();

    public interface TickBehavior {
        void tick(Block object);
    }

    public interface PlaceBehavior {
        default boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onPlaced(World world, Entity entity, BlockPos blockPos, Block block);
    }

    public interface ActiveBehavior { // right click entity
        default boolean shouldActivated(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onActivated(World world, Entity entity, BlockPos pos, Block block);
    }

    // TODO:
    public interface TouchBehavior { // left click entity
        default boolean onTouch(Block block) { return false; }

        void onTouched(Block block);
    }

    public interface DestroyBehavior {
        default boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block);
    }

}
