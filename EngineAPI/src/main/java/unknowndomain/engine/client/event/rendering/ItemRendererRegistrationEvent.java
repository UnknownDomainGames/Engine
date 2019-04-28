package unknowndomain.engine.client.event.rendering;

import unknowndomain.engine.client.rendering.item.ItemRenderManager;
import unknowndomain.engine.client.rendering.item.ItemRenderer;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.item.Item;

public class ItemRendererRegistrationEvent implements Event {

    private final ItemRenderManager itemRenderManager;

    public ItemRendererRegistrationEvent(ItemRenderManager itemRenderManager) {
        this.itemRenderManager = itemRenderManager;
    }

    public void register(Item item, ItemRenderer renderer) {
        itemRenderManager.register(item, renderer);
    }
}
