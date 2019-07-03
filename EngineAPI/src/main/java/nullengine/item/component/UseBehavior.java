package nullengine.item.component;

import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.item.ItemStack;

public interface UseBehavior extends Component {
    void onUseStart(Entity player, ItemStack itemStack);

    default boolean onUsing(Entity player, ItemStack itemStack, int tickElapsed) {
        return false;
    }

    default void onUseStop(Entity player, ItemStack itemStack, int tickElapsed) {
    }
}
