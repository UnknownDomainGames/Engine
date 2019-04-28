package unknowndomain.engine.entity.item;

import org.joml.Vector3dc;
import unknowndomain.engine.entity.EntityBase;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.world.World;

public class EntityItem extends EntityBase {

    private ItemStack itemStack;

    public EntityItem(int id, World world) {
        super(id, world);
    }

    public EntityItem(int id, World world, Vector3dc position) {
        super(id, world, position);
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
