package nullengine.entity;

import nullengine.entity.component.TwoHands;
import nullengine.item.ItemStack;
import nullengine.world.World;
import org.joml.Vector3dc;

public class CameraEntity extends BaseEntity {

    public CameraEntity(int id, World world, Vector3dc position) {
        super(id, world, position);
        setComponent(TwoHands.class, new TwoHands.Impl());
        getComponent(TwoHands.class).ifPresent(twoHands -> twoHands.setMainHand(ItemStack.EMPTY));
    }
}
