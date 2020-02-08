package engine.item.component;

import engine.component.Component;
import engine.event.block.cause.BlockInteractCause;
import engine.item.ItemStack;
import engine.world.hit.BlockHitResult;

public interface ClickBlockBehavior extends Component {
    void onClicked(ItemStack itemStack, BlockHitResult hit, BlockInteractCause cause);
}
