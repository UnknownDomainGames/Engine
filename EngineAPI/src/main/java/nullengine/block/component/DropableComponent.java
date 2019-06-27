package nullengine.block.component;

import com.google.common.collect.Lists;
import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.item.ItemStack;
import nullengine.math.BlockPos;

import java.util.List;

public interface DropableComponent extends Component {
    default List<ItemStack> getDropItems(Entity entity, BlockPos pos, Block block){
        return Lists.newArrayList();
    }
}
