package unknowndomain.engine.block;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BlockPrototype {
    // all these behaviors are missing arguments
    // fill those arguments later

    public static ActivateBehavior DEFAULT_ACTIVATE = new ActivateBehavior() {
    };
    public static ClickBehavior DEFAULT_CLICK = new ClickBehavior() {
    };
    public static PlaceBehavior DEFAULT_PLACE = (world, entity, blockPos, block, cause) -> {
    };
    public static DestroyBehavior DEFAULT_DESTROY = (world, entity, blockPos, block, cause) -> {
    };

    public abstract List<Block> getAllStates();

    public interface RandomTickListener extends Component {
        void onRandomTick(World world, BlockPos pos, Block block);
    }

    /**
     * Call when the block is left clicked.
     */
    public interface ClickBehavior extends Component {
        default boolean onClicked(World world, BlockPos pos, Block block) {
            return false;
        }
    }

    /**
     * Call when the block is right clicked.
     */
    public interface ActivateBehavior extends Component {
        default boolean onActivated(World world, Entity entity, BlockPos pos, Block block) {
            return false;
        }
    }

    public interface ChangeListener extends Component {
        void onChange(World world, BlockPos pos, Block block, BlockChangeCause cause);
    }

    public interface NeighborChangeListener extends Component {
        void onNeighborChange(World world, BlockPos pos, Block block, Facing face, BlockPos neighborPos, Block neighbor, BlockChangeCause cause);
    }

    public interface PlaceBehavior extends Component {
        default boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
            return true;
        }

        void onPlaced(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause);
    }

    public interface DestroyBehavior extends Component {
        default boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
            return true;
        }

        void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause);

        default DestroyableProperty getProperty(World world, Entity entity, BlockPos blockPos, Block block){
            return DEFAULT_PROPERTY;
        }
        DestroyableProperty DEFAULT_PROPERTY = new DestroyableProperty();
        class DestroyableProperty{
            public float hardness;
            public float explosionResistance;
            public Map<String, Integer> toolRequired = new HashMap<>();

        }
    }

}
