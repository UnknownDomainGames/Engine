package nullengine.item.component;

import nullengine.component.Component;
import nullengine.item.ItemStack;
import nullengine.player.Player;
import nullengine.world.collision.RayTraceBlockHit;

public interface UseBlockBehavior extends Component {
    void onUseBlockStart(Player player, ItemStack itemStack, RayTraceBlockHit hit);

    default boolean onUsingBlock(Player player, ItemStack itemStack, RayTraceBlockHit hit, int tickElapsed) {
        return false;
    }

    default void onUseBlockStop(Player player, ItemStack itemStack, RayTraceBlockHit hit, int tickElapsed) {
    }
}
