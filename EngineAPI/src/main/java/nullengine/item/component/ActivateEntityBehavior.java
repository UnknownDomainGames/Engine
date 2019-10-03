package nullengine.item.component;

import nullengine.component.Component;
import nullengine.event.entity.cause.EntityInteractCause;
import nullengine.item.ItemStack;
import nullengine.world.hit.EntityHitResult;

public interface ActivateEntityBehavior extends Component {

    void onActivate(ItemStack itemStack, EntityHitResult hit, EntityInteractCause cause);
}
