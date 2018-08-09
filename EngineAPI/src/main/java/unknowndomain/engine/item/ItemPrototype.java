package unknowndomain.engine.item;

import unknowndomain.engine.Entity;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.world.World;

public interface ItemPrototype extends Prototype<Item, Player> {
    interface UseBehavior {
        void onUseStart(World world, Player player, Item item);

        default boolean onUsing(World world, Player player, Item item, int tickElapsed) {
            return false;
        }

        default void onUseStop(World world, Player player, Item item, int tickElapsed) {
        }
    }

    interface UseBlockBehavior {
        void onUseBlockStart(World world, Player player, Item item, Block.Hit hit);

        default boolean onUsingBlock(Player player, Item item, Block.Hit hit, int tickElapsed) {
            return false;
        }

        default void onUseBlockStop(Player player, Item item, Block.Hit hit, int tickElapsed) {
        }
    }

    interface HitBlockBehavior {
        void onHit(Player player, Item item, Block.Hit hit);

//        boolean onKeep(Player player, Item item, Block.Hit hit, int tickElapsed);

//        void onUseStop(Player player, Item item, Block.Hit hit, int tickElapsed);
    }

    interface HitEntityBehavior {
        void onStart(Player player, Item item, Entity entity);

        boolean onKeep(Player player, Item item, Entity entity, int tickElapsed);

        void onStart(Player player, Item item, Entity entity, int tickElapsed);
    }
}
