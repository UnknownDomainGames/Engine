package unknowndomain.engine.entity;

import unknowndomain.engine.entity.component.TwoHands;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.world.World;
import unknowndomain.game.init.Blocks;

public class EntityPlayer extends EntityBase {

    public EntityPlayer(int id, World world) {
        super(id, world);
        setComponent(TwoHands.class, new TwoHands.Impl());
        getComponent(TwoHands.class).ifPresent(twoHands -> twoHands.setMainHand(new ItemStack(new ItemBlock(Blocks.DIRT),1)));//TODO debug purpose
    }
}
