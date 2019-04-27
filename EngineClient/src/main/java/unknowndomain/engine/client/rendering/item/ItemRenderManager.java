package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemRenderManager {

    private final Map<Item, ItemRenderer> itemRendererMap = new HashMap<>();

    private final ItemRendererTest defaultItemRenderer = new ItemRendererTest();

    public void register(Item item, ItemRenderer renderer) {
        if (itemRendererMap.containsKey(item))
            throw new IllegalArgumentException();

        itemRendererMap.put(item, renderer);
    }

    public void init(RenderContext context) {
        defaultItemRenderer.init(context);
    }

    public void render(ItemStack itemStack, float partial) {
        if (itemStack.isEmpty())
            return;

        itemRendererMap.getOrDefault(itemStack.getItem(), defaultItemRenderer).render(itemStack, partial);
    }

    public void dispose() {
        defaultItemRenderer.dispose();
    }
}
