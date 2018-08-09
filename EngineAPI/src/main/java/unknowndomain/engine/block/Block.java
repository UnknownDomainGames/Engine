package unknowndomain.engine.block;

import com.google.common.collect.ImmutableList;
import org.joml.Vector3f;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.World;

import java.util.List;
import java.util.Optional;

public abstract class Block implements Prototype<BlockObject, World> {
    // all these behaviors are missing arguments
    // fill those arguments later

    public abstract List<BlockObject> getAllStates();

    public static class Hit {
        public final BlockPos position;
        public final BlockObject block;
        public final Vector3f hit;
        public final Facing face;

        public Hit(BlockPos position, BlockObject block, Vector3f hit, Facing face) {
            this.position = position;
            this.block = block;
            this.hit = hit;
            this.face = face;
        }
    }

    public interface TickBehavior {
        void tick(BlockObject object);
    }

    public interface PlaceBehavior {
        boolean onPrePlace(BlockObject block);

        void onPlaced(BlockObject block);
    }

    public interface ActiveBehavior { // right click entity
        boolean onActivate(BlockObject block);

        void onActivated(BlockObject block);
    }

    public interface TouchBehavior { // left click entity
        boolean onTouch(BlockObject block);

        void onTouched(BlockObject block);
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
