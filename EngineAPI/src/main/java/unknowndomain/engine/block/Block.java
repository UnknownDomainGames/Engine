package unknowndomain.engine.block;

import com.google.common.collect.ImmutableList;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.world.World;

import java.util.List;
import java.util.Optional;

public abstract class Block implements Prototype<BlockObject, World> {
    // all these behaviors are missing arguments
    // fill those arguments later

    public abstract List<BlockObject> getAllStates();

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
