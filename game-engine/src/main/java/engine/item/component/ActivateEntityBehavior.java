package engine.item.component;

import engine.component.Component;
import engine.event.entity.cause.EntityInteractCause;
import engine.item.ItemStack;
import engine.world.hit.EntityHitResult;

public interface ActivateEntityBehavior extends Component {

    void onActivate(ItemStack itemStack, EntityHitResult hit, EntityInteractCause cause);
}
