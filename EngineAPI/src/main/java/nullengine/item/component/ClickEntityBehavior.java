package nullengine.item.component;

import nullengine.component.Component;
import nullengine.event.entity.cause.EntityInteractCause;
import nullengine.item.ItemStack;
import nullengine.world.hit.EntityHitResult;

public interface ClickEntityBehavior extends Component {
    void onClicked(ItemStack itemStack, EntityHitResult hit, EntityInteractCause cause);
}
