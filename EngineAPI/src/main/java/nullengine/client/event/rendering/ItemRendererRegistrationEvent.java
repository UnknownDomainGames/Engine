package nullengine.client.event.rendering;

import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.item.ItemRenderer;
import nullengine.event.Event;
import nullengine.item.Item;

public class ItemRendererRegistrationEvent implements Event {

    private final ItemRenderManager itemRenderManager;

    public ItemRendererRegistrationEvent(ItemRenderManager itemRenderManager) {
        this.itemRenderManager = itemRenderManager;
    }

    public void register(Item item, ItemRenderer renderer) {
        itemRenderManager.register(item, renderer);
    }
}
