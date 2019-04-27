package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemStack;

public interface ItemRenderManager {
    void register(Item item, ItemRenderer renderer);

    void render(ItemStack itemStack, float partial);
}
