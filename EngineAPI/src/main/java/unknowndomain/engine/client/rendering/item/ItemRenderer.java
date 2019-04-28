package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.item.ItemStack;

public interface ItemRenderer {

    void init(RenderContext context);

    void render(ItemStack itemStack, float partial);

    void dispose();
}
