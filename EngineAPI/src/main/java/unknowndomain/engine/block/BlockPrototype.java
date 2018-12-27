package unknowndomain.engine.block;

import com.google.common.collect.ImmutableList;
import org.joml.Vector3f;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.World;

import java.util.List;
import java.util.Optional;

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

    public static class Hit {
        private final BlockPos pos;
        private final Block block;
        private final Vector3f hit;
        private final Facing face;

        public Hit(BlockPos pos, Block block, Vector3f hit, Facing face) {
            this.pos = pos;
            this.block = block;
            this.hit = hit;
            this.face = face;
        }

        public Block getBlock() {
            return block;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Facing getFace() {
            return face;
        }

        public Vector3f getHit() {
            return hit;
        }
    }

    public interface Property<T extends Comparable<T>> {
        String getName();

        ImmutableList<T> getValues();

        /**
         * The class of the values of this property
         */
        Class<T> getValueClass();

        Optional<T> parseValue(String value);

        /**
         * Get the name for the given value.
         */
        String getName(T value);
    }

}
