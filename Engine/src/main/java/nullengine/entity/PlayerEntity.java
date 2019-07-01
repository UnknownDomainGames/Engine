package nullengine.entity;

import nullengine.entity.component.TwoHands;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;
import nullengine.world.World;
import unknowndomaingame.foundation.init.Blocks;

public class PlayerEntity extends BaseEntity {

    public PlayerEntity(int id, World world) {
        super(id, world);
        setComponent(TwoHands.class, new TwoHands.Impl());
        getComponent(TwoHands.class).ifPresent
            (twoHands -> twoHands.setMainHand(new ItemStack(new BlockItem(Blocks.DIRT), 1)));//TODO debug purpose
    }

}
