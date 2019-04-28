package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.component.Owner;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemStack;

@Owner(RenderContext.class)
public interface ItemRenderManager extends Component {
    void register(Item item, ItemRenderer renderer);

    void render(ItemStack itemStack, float partial);
}
