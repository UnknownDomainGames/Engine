package unknowndomain.engine.entity.item;

import org.joml.Vector3dc;
import unknowndomain.engine.entity.BaseEntity;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.world.World;

public class ItemEntity extends BaseEntity {

    private ItemStack itemStack;

    public ItemEntity(int id, World world, ItemStack itemStack) {
        super(id, world);
        this.itemStack = itemStack;
    }

    public ItemEntity(int id, World world, Vector3dc position, ItemStack itemStack) {
        super(id, world, position);
        this.itemStack = itemStack;
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
