package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.item.ItemStack;

public interface ItemRenderer {

    void render(ItemStack itemStack, float partial);
}
