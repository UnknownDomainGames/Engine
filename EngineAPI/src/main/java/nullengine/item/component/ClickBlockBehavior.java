package nullengine.item.component;

import nullengine.component.Component;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.item.ItemStack;
import nullengine.world.collision.RayTraceBlockHit;

public interface ClickBlockBehavior extends Component {
    void onClicked(ItemStack itemStack, RayTraceBlockHit hit, BlockInteractCause cause);
}
