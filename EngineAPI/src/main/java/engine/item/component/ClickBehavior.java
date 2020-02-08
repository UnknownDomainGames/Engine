package engine.item.component;

import engine.component.Component;
import engine.event.item.cause.ItemInteractCause;
import engine.item.ItemStack;

public interface ClickBehavior extends Component {

    void onClicked(ItemStack itemStack, ItemInteractCause cause);
}
