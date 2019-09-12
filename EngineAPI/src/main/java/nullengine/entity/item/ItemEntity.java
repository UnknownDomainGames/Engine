package nullengine.entity.item;

import nullengine.entity.BaseEntity;
import nullengine.item.ItemStack;
import nullengine.world.World;

public class ItemEntity extends BaseEntity {

    private ItemStack itemStack;

    public ItemEntity(int id, World world, double x, double y, double z) {
        super(id, world, x, y, z);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void tick() {
        super.tick();
    }
}
