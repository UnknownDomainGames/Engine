package nullengine.item.component;

import nullengine.component.Component;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.item.ItemStack;
import nullengine.world.hit.BlockHitResult;

public interface ClickBlockBehavior extends Component {
    void onClicked(ItemStack itemStack, BlockHitResult hit, BlockInteractCause cause);
}
