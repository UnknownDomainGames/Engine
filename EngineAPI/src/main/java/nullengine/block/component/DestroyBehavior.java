package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

import java.util.HashMap;
import java.util.Map;

public interface DestroyBehavior extends Component {
    default boolean canDestroy(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause) {
        return true;
    }

    void onDestroyed(World world, Entity entity, BlockPos blockPos, Block block, BlockChangeCause cause);

    default DestroyableProperty getProperty(World world, Entity entity, BlockPos blockPos, Block block) {
        return DEFAULT_PROPERTY;
    }

    DestroyableProperty DEFAULT_PROPERTY = new DestroyableProperty();

    class DestroyableProperty {
        public float hardness;
        public float explosionResistance;
        public Map<String, Integer> toolRequired = new HashMap<>();

    }
}
