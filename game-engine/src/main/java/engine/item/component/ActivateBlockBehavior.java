package engine.item.component;

import engine.component.Component;
import engine.event.block.cause.BlockInteractCause;
import engine.item.ItemStack;
import engine.world.hit.BlockHitResult;

public interface ActivateBlockBehavior extends Component {
    void onActivate(ItemStack itemStack, BlockHitResult hit, BlockInteractCause cause);
}
