package engine.item.component;

import engine.component.Component;
import engine.item.ItemStack;
import engine.player.Player;

public interface UseBehavior extends Component {
    void onUseStart(Player player, ItemStack itemStack);

    default boolean onUsing(Player player, ItemStack itemStack, int tickElapsed) {
        return false;
    }

    default void onUseStop(Player player, ItemStack itemStack, int tickElapsed) {
    }
}
