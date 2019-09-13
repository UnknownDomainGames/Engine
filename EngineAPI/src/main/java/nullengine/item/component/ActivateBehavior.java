package nullengine.item.component;

import nullengine.component.Component;
import nullengine.event.item.cause.ItemInteractCause;
import nullengine.item.ItemStack;

public interface ActivateBehavior extends Component {

    void onActivate(ItemStack itemStack, ItemInteractCause cause);
}
