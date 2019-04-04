package unknowndomain.engine.item;

import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.player.Player;

public interface ItemPrototype {
    UseBehavior DEFAULT_USE = (player, itemStack) -> {
    };
    UseBlockBehavior DEFAULT_USE_BLOCK = (player, item, hit) -> {
    };
    HitBlockBehavior DEFAULT_HIT_BLOCK = (player, itemStack, hit) -> {
    };

    interface UseBehavior extends Component {
        void onUseStart(Player player, ItemStack itemStack);

        default boolean onUsing(Player player, ItemStack itemStack, int tickElapsed) {
            return false;
        }

        default void onUseStop(Player player, ItemStack itemStack, int tickElapsed) {
        }
    }

    interface UseBlockBehavior extends Component {
        void onUseBlockStart(Player player, ItemStack itemStack, RayTraceBlockHit hit);

        default boolean onUsingBlock(Player player, ItemStack itemStack, RayTraceBlockHit hit, int tickElapsed) {
            return false;
        }

        default void onUseBlockStop(Player player, ItemStack itemStack, RayTraceBlockHit hit, int tickElapsed) {
        }
    }

    interface HitBlockBehavior {
        void onHit(Player player, ItemStack itemStack, RayTraceBlockHit hit);

//        boolean onKeep(Player player, Item item, BlockPrototype.Hit hit, int tickElapsed);

//        void onUseStop(Player player, Item item, BlockPrototype.Hit hit, int tickElapsed);
    }

    interface HitEntityBehavior extends Component {
        void onStart(Player player, Item item, Entity entity);

        boolean onKeep(Player player, Item item, Entity entity, int tickElapsed);

        void onStart(Player player, Item item, Entity entity, int tickElapsed);
    }
}
