package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderContext;
import nullengine.component.Component;
import nullengine.component.Owner;
import nullengine.item.Item;
import nullengine.item.ItemStack;

@Owner(RenderContext.class)
public interface ItemRenderManager extends Component {
    void register(Item item, ItemRenderer renderer);

    void render(ItemStack itemStack, float partial);
}
