package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.client.event.rendering.ItemRendererRegistrationEvent;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemRenderManagerImpl implements ItemRenderManager {

    private final Map<Item, ItemRenderer> itemRendererMap = new HashMap<>();

    private final ItemRendererTest defaultItemRenderer = new ItemRendererTest();

    @Override
    public void register(Item item, ItemRenderer renderer) {
        if (itemRendererMap.containsKey(item))
            throw new IllegalArgumentException();

        itemRendererMap.put(item, renderer);
    }

    public void init(RenderContext context) {
        defaultItemRenderer.init(context);

        context.getEngine().getCurrentGame().getEventBus().post(new ItemRendererRegistrationEvent(this));
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        if (itemStack.isEmpty())
            return;

        itemRendererMap.getOrDefault(itemStack.getItem(), defaultItemRenderer).render(itemStack, partial);
    }

    public void dispose() {
        defaultItemRenderer.dispose();
    }
}
