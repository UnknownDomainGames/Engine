package nullengine.entity;

import nullengine.entity.component.TwoHands;
import nullengine.item.ItemStack;
import nullengine.world.World;

public class PlayerEntity extends BaseEntity {

    public PlayerEntity(int id, World world) {
        super(id, world);
        setComponent(TwoHands.class, new TwoHands.Impl());
        getComponent(TwoHands.class).ifPresent(twoHands -> twoHands.setMainHand(ItemStack.EMPTY));//TODO debug purpose
    }
}
