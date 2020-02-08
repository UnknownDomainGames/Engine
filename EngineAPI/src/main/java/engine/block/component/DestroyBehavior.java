package engine.block.component;

import engine.block.Block;
import engine.component.Component;
import engine.event.block.cause.BlockChangeCause;
import engine.item.ItemStack;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DestroyBehavior extends Component {
    default boolean canDestroy(World world, BlockPos blockPos, Block block, BlockChangeCause cause) {
        return true;
    }

    void onDestroyed(World world, BlockPos blockPos, Block block, BlockChangeCause cause);

    default boolean canDropItem(World world, BlockPos pos, Block block, BlockChangeCause cause) {
        return true;
    }

    default List<ItemStack> getDropItems(World world, BlockPos pos, Block block, BlockChangeCause cause) {
        var item = Registries.getItemRegistry().getBlockItem(block);
        return item.map(blockItem -> List.of(new ItemStack(blockItem, 1))).orElseGet(List::of);
    }

    default DestroyableProperty getProperty(World world, BlockPos blockPos, Block block) {
        return DEFAULT_PROPERTY;
    }

    DestroyableProperty DEFAULT_PROPERTY = new DestroyableProperty();

    class DestroyableProperty {
        public float hardness;
        public float explosionResistance;
        public Map<String, Integer> toolRequired = new HashMap<>();

    }
}
