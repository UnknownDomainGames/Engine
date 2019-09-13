package nullengine.item.component;

import nullengine.component.Component;
import nullengine.event.item.cause.ItemInteractCause;
import nullengine.item.ItemStack;

public interface ClickBehavior extends Component {

    void onClicked(ItemStack itemStack, ItemInteractCause cause);
}
