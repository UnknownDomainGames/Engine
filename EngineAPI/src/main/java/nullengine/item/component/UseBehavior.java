package nullengine.item.component;

import nullengine.component.Component;
import nullengine.item.ItemStack;
import nullengine.player.Player;

public interface UseBehavior extends Component {
    void onUseStart(Player player, ItemStack itemStack);

    default boolean onUsing(Player player, ItemStack itemStack, int tickElapsed) {
        return false;
    }

    default void onUseStop(Player player, ItemStack itemStack, int tickElapsed) {
    }
}
