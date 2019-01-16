package unknowndomain.engine.block;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import java.util.List;

public abstract class BlockPrototype {
    // all these behaviors are missing arguments
    // fill those arguments later

    public static ActivateBehavior DEFAULT_ACTIVATE = (world, entity, pos, block) -> {
    };
    public static ClickBehavior DEFAULT_CLICK = (world, pos, block) -> {
    };
    public static PlaceBehavior DEFAULT_PLACE = (world, entity, blockPos, block) -> {
    };
    public static DestroyBehavior DEFAULT_DESTROY = (world, entity, blockPos, block) -> {
    };

    public abstract List<Block> getAllStates();

    public interface RandomTickListener extends Component {
        void onRandomTick(World world, BlockPos pos, Block block);
    }

    /**
     * Call when the block is left clicked.
     */
    public interface ClickBehavior extends Component {
        default boolean canClick(World world, BlockPos pos, Block block) {
            return false;
        }

        void onClicked(World world, BlockPos pos, Block block);
    }

    /**
     * Call when the block is right clicked.
     */
    public interface ActivateBehavior extends Component {
        default boolean canActivate(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onActivated(World world, Entity entity, BlockPos pos, Block block);
    }

    public interface ChangeListener extends Component {
        void onChange(World world, BlockPos pos, Block block, BlockChangeCause cause);
    }

    public interface PlaceBehavior extends Component {
        default boolean canPlace(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onPlaced(World world, Entity entity, BlockPos blockPos, Block block);
    }

    public interface DestroyBehavior extends Component {
        default boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block) {
            return true;
        }

        void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block);
    }

}
