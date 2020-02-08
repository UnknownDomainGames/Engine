package engine.item.component;

import engine.component.Component;
import engine.event.item.cause.ItemInteractCause;
import engine.item.ItemStack;

public interface ActivateBehavior extends Component {

    void onActivate(ItemStack itemStack, ItemInteractCause cause);
}
