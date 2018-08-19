package unknowndomain.engine.block;

import com.google.common.collect.ImmutableList;
import org.joml.Vector3f;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.World;

import java.util.List;
import java.util.Optional;

public abstract class BlockPrototype implements Prototype<Block, World> {
    // all these behaviors are missing arguments
    // fill those arguments later

    public static PlaceBehavior DEFAULT_PLACE = (world, entity, block) -> {
    };
    public static ActiveBehavior DEFAULT_ACTIVE = (world, entity, pos, block) -> {
    };

    public abstract List<Block> getAllStates();

    public interface TickBehavior {
        void tick(Block object);
    }

    public interface PlaceBehavior {
        default boolean canPlace(World world, Entity entity, Block block) {
            return true;
        }

        void onPlaced(World world, Entity entity, Block block);
    }

    public interface ActiveBehavior { // right click entity
        default boolean shouldActivated(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onActivated(World world, Entity entity, BlockPos pos, Block block);
    }

    public interface TouchBehavior { // left click entity
        boolean onTouch(Block block);

        void onTouched(Block block);
    }

    public static class Hit {
        public final BlockPos position;
        public final Block block;
        public final Vector3f hit;
        public final Facing face;

        public Hit(BlockPos position, Block block, Vector3f hit, Facing face) {
            this.position = position;
            this.block = block;
            this.hit = hit;
            this.face = face;
        }
    }

    public interface DestroyBehavior {

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
