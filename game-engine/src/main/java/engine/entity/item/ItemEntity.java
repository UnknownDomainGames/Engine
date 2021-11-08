package engine.entity.item;

import engine.entity.BaseEntity;
import engine.item.ItemStack;
import engine.world.World;
import org.joml.primitives.AABBd;

public class ItemEntity extends BaseEntity {

    private ItemStack itemStack;

    public ItemEntity(int id, World world, double x, double y, double z) {
        super(id, world, x, y, z);
        setBoundingBox(new AABBd(-.125, 0, -.125, .125, .125, .125));
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
