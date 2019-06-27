package nullengine.item.component;

import nullengine.component.Component;
import nullengine.item.ItemStack;
import nullengine.player.Player;
import nullengine.world.collision.RayTraceBlockHit;

public interface HitBlockBehavior extends Component {
    void onHit(Player player, ItemStack itemStack, RayTraceBlockHit hit);

    default boolean onKeep(Player player, ItemStack itemStack, RayTraceBlockHit hit, int tickElapsed) {
        return false;
    }

    default void onHitStop(Player player, ItemStack itemStack, RayTraceBlockHit hit) {
    }
}
