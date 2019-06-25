package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderContext;
import nullengine.item.ItemStack;

public interface ItemRenderer {

    void init(RenderContext context);

    void render(ItemStack itemStack, float partial);

    void dispose();
}
